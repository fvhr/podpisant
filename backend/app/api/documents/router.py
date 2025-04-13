import io
import json
import os
import secrets
from datetime import datetime, timezone, timedelta
from hashlib import sha256
from zoneinfo import ZoneInfo

from fastapi import APIRouter, UploadFile, File, Form, Depends, HTTPException, Response
from minio import Minio
from pydantic import Field
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload, session

from api.auth.idp import get_current_user
from api.auth.user_service import get_user_orgs_with_admin_and_tags
from api.documents.schemas import CreateDocumentSchema, DocumentSchema, AddStagesToDocumentSchema, \
    DocSignStageCreateSchema, DocumentStageDetailSchema, StageSignerInfoSchema, SignDocumentRequest, \
    DigitalSignatureSchema
from database.models import User, Document, DocSignStage, StageSigner, Department, UserDepartment
from database.models.document import DocSignStatus
from database.session_manager import get_db
from minio_client.client import get_minio_client, DOCUMENTS_BUCKET_NAME, MINIO_PUBLIC_URL

documents_router = APIRouter(prefix="/documents", tags=["documents"])


@documents_router.post("")
async def upload_document(
    file: UploadFile = File(...),
    item_json: str = Form(...),
    minio_client: Minio = Depends(get_minio_client),
    session: AsyncSession = Depends(get_db),
    user: User = Depends(get_current_user)
):
    item_data = CreateDocumentSchema(**json.loads(item_json))
    document_name = item_data.name
    organization_id = item_data.organization_id


    file_bytes = await file.read()

    if not minio_client.bucket_exists(DOCUMENTS_BUCKET_NAME):
        minio_client.make_bucket(DOCUMENTS_BUCKET_NAME)

    MOSCOW_TZ = ZoneInfo("Europe/Moscow")
    created_at = datetime.now(MOSCOW_TZ)
    document = Document(
        name=document_name,
        organization_id=organization_id,
        creator_id=user.id,
        created_at=created_at,

    )
    document = await session.merge(document)
    await session.flush()
    unique_filename = f"document_{document.id}"
    minio_client.put_object(
        bucket_name=os.getenv("DOCUMENTS_BUCKET_NAME"),
        object_name=unique_filename,
        data=io.BytesIO(file_bytes),
        length=len(file_bytes),
        content_type=file.content_type
    )
    await session.commit()

    file_url = f"{MINIO_PUBLIC_URL}/{DOCUMENTS_BUCKET_NAME}/{unique_filename}"

    return {
        "document_id": document.id,
        "file_url": file_url
    }


@documents_router.get("/{document_id}/file")
async def get_document_file(
    document_id: int,
    minio_client: Minio = Depends(get_minio_client),
    session: AsyncSession = Depends(get_db),
    # user: User = Depends(get_current_user)
):
    document = await session.get(Document, document_id)
    if not document:
        raise HTTPException(status_code=404, detail="Document not found")

    object_name = f"document_{document_id}"

    response = minio_client.get_object(
        bucket_name=DOCUMENTS_BUCKET_NAME,
        object_name=object_name
    )

    file_data = response.read()

    content_type = response.headers.get("content-type", "application/octet-stream")

    return Response(
        content=file_data,
        media_type=content_type,
        headers={
            "Content-Disposition": f"attachment; filename={object_name}",
            "Document-ID": str(document_id)
        }
    )


@documents_router.get("")
async def get_document(user: User = Depends(get_current_user), session: AsyncSession = Depends(get_db)) -> list[DocumentSchema]:
    org_ids, _, _ = await get_user_orgs_with_admin_and_tags(session, user.id)
    documents = await session.execute(select(Document).where(Document.organization_id.in_(org_ids)))
    documents = documents.scalars().all()
    data = [
        DocumentSchema.from_db(document) for document in documents
    ]
    return data


@documents_router.get("/organizations/{organization_id}")
async def get_documents_by_organization_id(organization_id: int, session: AsyncSession = Depends(get_db)) -> list[DocumentSchema]:
    documents = await session.execute(select(Document).where(Document.organization_id == organization_id))
    documents = documents.scalars().all()
    data = [
        DocumentSchema.from_db(document) for document in documents
    ]
    return data


@documents_router.get("/{document_id}")
async def get_document_by_id(document_id: int, session: AsyncSession = Depends(get_db)) -> DocumentSchema:
    document = await session.get(Document, document_id)
    if not document:
        raise HTTPException(status_code=404, detail="Document not found")
    data = DocumentSchema.from_db(document)
    return data


@documents_router.post("/{document_id}/stages")
async def add_stages_to_document(
    data: list[AddStagesToDocumentSchema],
    document_id: int,
    session: AsyncSession = Depends(get_db)
) -> list[DocSignStageCreateSchema]:
    try:
        result = []

        document = await session.get(Document, document_id)
        if not document:
            raise HTTPException(status_code=404, detail="Document not found")

        existing_stages = (await session.execute(
            select(DocSignStage)
            .where(DocSignStage.doc_id == document_id)
            .options(selectinload(DocSignStage.signers))
        )).scalars().all()

        existing_numbers = {stage.stage_number for stage in existing_stages}

        MOSCOW_TZ = ZoneInfo("Europe/Moscow")
        created_at = datetime.now(MOSCOW_TZ)

        for stage_data in data:
            if stage_data.number in existing_numbers:
                raise HTTPException(
                    status_code=400,
                    detail=f"Stage number {stage_data.number} already exists"
                )

            stage_db = DocSignStage(
                name=stage_data.name,
                doc_id=document_id,
                deadline=stage_data.deadline,
                stage_number=stage_data.number,
                is_current=False,
                created_at=created_at,
            )

            session.add(stage_db)
            await session.flush()

            if stage_data.user_ids:
                for user_id in stage_data.user_ids:
                    user = await session.get(User, user_id)
                    if not user:
                        raise HTTPException(
                            status_code=404,
                            detail=f"User with ID {user_id} not found"
                        )

                    signer = StageSigner(
                        user_id=user_id,
                        stage_id=stage_db.id,
                    )
                    session.add(signer)

            result.append(stage_db)
            existing_numbers.add(stage_data.number)

        if not existing_stages and result:
            first_stage = result[0]
            first_stage.is_current = True
            document.current_stage_id = first_stage.id

        await session.commit()

        return [await DocSignStageCreateSchema.from_db(stage, session) for stage in result]

    except HTTPException:
        await session.rollback()
        raise
    except Exception as e:
        await session.rollback()
        raise HTTPException(status_code=500, detail=str(e))


@documents_router.get("/{document_id}/stages", response_model=list[DocumentStageDetailSchema])
async def get_document_stages_with_signers(
    document_id: int,
    session: AsyncSession = Depends(get_db)
) -> list[DocumentStageDetailSchema]:
    try:
        document = await session.get(Document, document_id)
        if not document:
            raise HTTPException(status_code=404, detail="Document not found")

        stages = (await session.execute(
            select(DocSignStage)
            .where(DocSignStage.doc_id == document_id)
            .options(
                selectinload(DocSignStage.signers).joinedload(StageSigner.user),
                selectinload(DocSignStage.document)
            )
            .order_by(DocSignStage.stage_number)
        )).scalars().all()

        result = []
        for stage in stages:
            signatures = []
            signed_count = 0
            rejected_count = 0

            for signer in stage.signers:
                # Формируем информацию о подписи/отклонении
                digital_signature = None
                if signer.digital_signature:
                    signature_id = f"{document_id}:{stage.id}:{signer.user_id}"
                    digital_signature = DigitalSignatureSchema(
                        signature_id=signature_id,
                        signed_at=signer.signed_at,
                        is_valid=True
                    )

                signer_info = StageSignerInfoSchema(
                    user_id=signer.user_id,
                    fio=signer.user.fio,
                    email=signer.user.email,
                    signed_at=signer.signed_at,
                    rejected_at=signer.rejected_at,
                    signature_type=signer.signature_type,
                    digital_signature=digital_signature
                )

                signatures.append(signer_info)
                if signer.signed_at:
                    signed_count += 1
                if signer.rejected_at:
                    rejected_count += 1

            is_completed = (signed_count == len(stage.signers) or rejected_count > 0) and len(stage.signers) > 0

            result.append(DocumentStageDetailSchema(
                id=stage.id,
                name=stage.name,
                number=stage.stage_number,
                deadline=stage.deadline,
                is_current=stage.is_current,
                created_at=stage.created_at,
                is_completed=is_completed,
                is_rejected=rejected_count == len(stage.signers),
                signatures=signatures,
                signed_count=signed_count,
                total_signers=len(stage.signers)
            ))

        return result

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@documents_router.post("/{document_id}/stages/{stage_id}/sign")
async def sign_document(
    document_id: int,
    stage_id: int,
    session: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
) -> dict:
    """
    Создаем цифровую подпись документа на основе данных пользователя из БД
    Только для текущего этапа (is_current=True)
    """
    try:
        # Получаем этап и проверяем, что он текущий
        stage = await session.get(DocSignStage, stage_id)
        if not stage:
            raise HTTPException(status_code=404, detail="Stage not found")

        if not stage.is_current:
            raise HTTPException(
                status_code=400,
                detail="Cannot sign - this is not the current stage"
            )

        # Получаем запись о подписании для текущего пользователя
        signer = (await session.execute(
            select(StageSigner)
            .join(StageSigner.user)
            .where(StageSigner.stage_id == stage_id)
            .where(StageSigner.user_id == current_user.id)
        )).scalar_one_or_none()

        if not signer or not signer.user:
            raise HTTPException(status_code=404, detail="Signing record or user not found")

        if signer.signed_at:
            raise HTTPException(status_code=400, detail="Document already signed")

        # Генерируем подпись на основе данных из БД
        salt = secrets.token_hex(32)
        sign_time = datetime.now(timezone.utc).isoformat()

        phone_hash = sha256((signer.user.phone + salt).encode()).hexdigest()
        fio_hash = sha256((signer.user.fio + salt).encode()).hexdigest()

        digital_signature = f"{salt}:{sign_time}:{phone_hash}:{fio_hash}"

        # Обновляем запись
        signer.signed_at = datetime.now(timezone.utc)
        signer.digital_signature = digital_signature
        signer.signature_type = "digital_auto"

        unsigned_count = (await session.execute(
            select(func.count())
            .select_from(StageSigner)
            .where(StageSigner.stage_id == stage_id)
            .where(StageSigner.signed_at == None)
        )).scalar()

        if unsigned_count == 0:
            # Находим следующий этап
            next_stage = (await session.execute(
                select(DocSignStage)
                .where(DocSignStage.doc_id == document_id)
                .where(DocSignStage.stage_number > stage.stage_number)
                .order_by(DocSignStage.stage_number)
                .limit(1)
            )).scalar_one_or_none()

            if next_stage:
                next_stage.is_current = True
                stage.is_current = False
            else:
                document = await session.get(Document, document_id)
                document.status = DocSignStatus.SIGNED
                stage.is_current = False

        await session.commit()

        return {
            "status": "signed",
            "signature_id": f"{document_id}:{stage_id}:{current_user.id}",
            "signed_at": signer.signed_at.isoformat(),
            "stage_completed": unsigned_count == 0
        }

    except Exception as e:
        await session.rollback()
        raise HTTPException(status_code=500, detail=str(e))

@documents_router.post("/{document_id}/stages/{stage_id}/reject")
async def reject_document(
    document_id: int,
    stage_id: int,
    session: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
) -> dict:
    """
    Отклонение подписания документа пользователем
    """
    try:
        stage = await session.get(DocSignStage, stage_id)
        if not stage:
            raise HTTPException(status_code=404, detail="Stage not found")

        if not stage.is_current:
            raise HTTPException(
                status_code=400,
                detail="Can only reject current stage documents"
            )

        signer = (await session.execute(
            select(StageSigner)
            .where(StageSigner.stage_id == stage_id)
            .where(StageSigner.user_id == current_user.id)
        )).scalar_one_or_none()

        if not signer:
            raise HTTPException(status_code=404, detail="Signing record not found")

        if signer.signed_at:
            raise HTTPException(status_code=400, detail="Document already signed")

        if signer.rejected_at:
            raise HTTPException(status_code=400, detail="Document already rejected")

        signer.rejected_at = datetime.now(timezone.utc)
        signer.signature_type = "rejected"

        # document = await session.get(Document, document_id)
        # document.status = DocSignStatus.REJECTED
        # document.rejected_by = current_user.id
        # document.rejected_at = datetime.now(timezone.utc)

        stage.is_current = False

        await session.commit()

        return {
            "status": "rejected",
            "document_id": document_id,
            "stage_id": stage_id,
            "rejected_at": signer.rejected_at.isoformat(),
            "rejected_by": str(current_user.id)
        }

    except Exception as e:
        await session.rollback()
        raise HTTPException(status_code=500, detail=str(e))


@documents_router.post("/{document_id}/broadcast/{dep_id}")
async def broadcast_to_department(
    document_id: int,
    dep_id: int,
    session: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
) -> dict:
    """
    Рассылает документ всем пользователям указанного департамента
    Создает общий этап подписания (stage_number=1) для всех сотрудников департамента
    """
    try:
        document = await session.get(Document, document_id)
        if not document:
            raise HTTPException(status_code=404, detail="Document not found")

        department = await session.get(Department, dep_id)
        if not department:
            raise HTTPException(status_code=404, detail="Department not found")

        # if not await check_department_admin(department.organization_id, current_user.id, session):
        #     raise HTTPException(status_code=403, detail="No permission to broadcast")

        # Получаем всех пользователей департамента
        users = (await session.execute(
            select(User)
            .join(UserDepartment)
            .where(UserDepartment.department_id == dep_id)
            .where(UserDepartment.user_id == User.id)
        )).scalars().all()

        if not users:
            raise HTTPException(status_code=400, detail="Department has no users")

        # Создаем этап подписания для департамента
        moscow_tz = ZoneInfo("Europe/Moscow")
        deadline = datetime.now(moscow_tz) + timedelta(days=7)  # Срок подписания 7 дней

        stage = DocSignStage(
            name=f"Подписание для {department.name}",
            doc_id=document_id,
            stage_number=1,
            created_at=datetime.now(moscow_tz),
            deadline=deadline,
            is_current=True
        )

        session.add(stage)
        await session.flush()  # Нужно для получения stage.id

        # Добавляем всех пользователей департамента как подписантов
        for user in users:
            signer = StageSigner(
                stage_id=stage.id,
                user_id=user.id,
                signature_type=None,
                signed_at=None
            )
            session.add(signer)

        document.status = DocSignStatus.IN_PROGRESS

        await session.commit()

        return {
            "status": "broadcasted",
            "document_id": document_id,
            "department_id": dep_id,
            "stage_id": stage.id,
            "users_count": len(users),
            "deadline": deadline.isoformat()
        }

    except Exception as e:
        await session.rollback()
        raise HTTPException(status_code=500, detail=str(e))

# async def check_department_admin(org_id: int, user_id: UUID, session: AsyncSession) -> bool:
#     """
#     Проверяет, является ли пользователь администратором организации или департамента
#     """
#     # Проверка на админа организации
#     is_org_admin = (await session.execute(
#         select(UserOrganization)
#         .where(UserOrganization.organization_id == org_id)
#         .where(UserOrganization.user_id == user_id)
#         .where(UserOrganization.is_admin == True)
#     )).scalar_one_or_none()
#
#     if is_org_admin:
#         return True
#
#     # Дополнительные проверки прав можно добавить здесь
#     return False
