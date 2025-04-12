from uuid import UUID

from pydantic import BaseModel

from database.models.document import DocumentTypeEnum, DocSignStatus


class CreateDocumentSchema(BaseModel):
    name: str
    organization_id: int


#     stages: list[StageSchema]
#
# class StageSchema(BaseModel):


class DocumentSchema(BaseModel):
    id: int
    name: str
    organization_id: int
    file_url: str
    created_at: str
    status: DocSignStatus
    type: DocumentTypeEnum | None
    creator_id: UUID
