import io
import json
import os
from datetime import datetime
from zoneinfo import ZoneInfo

from fastapi import APIRouter, UploadFile, File, Form, Depends
from minio import Minio
from pytz import UTC
from sqlalchemy.ext.asyncio import AsyncSession

from api.auth.idp import get_current_user
from api.documents.shcemas import CreateDocumentSchema
from database.models import User, Document
from database.session_manager import get_db
from minio_client.client import get_minio_client, DOCUMENTS_BUCKET_NAME, MINIO_ENDPOINT_URL

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
    unique_filename = f"document_{document.id}"
    # Загружаем в MinIO
    minio_client.put_object(
        bucket_name=os.getenv("DOCUMENTS_BUCKET_NAME"),
        object_name=unique_filename,
        data=io.BytesIO(file_bytes),
        length=len(file_bytes),
        content_type=file.content_type
    )
    await session.commit()

    file_url = f"{MINIO_ENDPOINT_URL}/{DOCUMENTS_BUCKET_NAME}/{unique_filename}"

    return {
        "document_id": document.id,
        "file_url": file_url
    }
