import os

from minio import Minio

MINIO_ENDPOINT_URL = os.getenv("MINIO_ENDPOINT")
MINIO_PUBLIC_URL = os.getenv("MINIO_PUBLIC_URL")
MINIO_ACCESS_KEY = os.getenv("MINIO_ACCESS_KEY")
MINIO_SECRET_KEY = os.getenv("MINIO_SECRET_KEY")
DOCUMENTS_BUCKET_NAME = os.getenv("DOCUMENTS_BUCKET_NAME")

async def get_minio_client() -> Minio:
    minio_client = Minio(
        endpoint=MINIO_ENDPOINT_URL,
        access_key=MINIO_ACCESS_KEY,
        secret_key=MINIO_SECRET_KEY,
        secure=os.getenv("MINIO_SECURE").lower() in ("true", "1"),
    )
    return minio_client
