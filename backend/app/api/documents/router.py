import io
import json
import os
from datetime import datetime
from zoneinfo import ZoneInfo

from fastapi import APIRouter, UploadFile, File, Form, Depends, HTTPException, Response
from minio import Minio
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from api.auth.idp import get_current_user
from api.auth.user_service import get_user_orgs_with_admin_and_tags
from api.documents.schemas import CreateDocumentSchema
from database.models import User, Document
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

    # if document.organization_id not in [org.id for org in user.organizations]:
    #     raise HTTPException(status_code=403, detail="Access denied")

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


# @documents_router.get("")
# async def get_document(user: User = Depends(get_current_user), session: AsyncSession = Depends(get_db)) -> UserDocumentsSchema:
#     org_ids, _, _ = await get_user_orgs_with_admin_and_tags(session, user.id)
#     documents = await session.execute(select(Document).where(Document.organization_id.in_(org_ids)))
#     return
