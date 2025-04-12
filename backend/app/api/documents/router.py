from fastapi import APIRouter

from api.documents.shcemas import CreateDocumentSchema

documents_router = APIRouter(prefix="/documents", tags=["documents"])


@documents_router.post("")
async def create_document(data: CreateDocumentSchema):

