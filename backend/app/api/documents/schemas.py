from uuid import UUID

from pydantic import BaseModel

from database.models.document import DocumentTypeEnum, DocSignStatus, Document


class CreateDocumentSchema(BaseModel):
    name: str
    organization_id: int


#     stages: list[StageSchema]
#
# class AddStagesSchema(BaseModel):



class DocumentSchema(BaseModel):
    id: int
    name: str
    organization_id: int
    file_url: str
    created_at: str
    status: DocSignStatus
    type: DocumentTypeEnum | None
    creator_id: UUID

    @classmethod
    def from_db(cls, document: Document) -> "DocumentSchema":
        return cls(
            id=document.id,
            name=document.name,
            organization_id=document.organization_id,
            creator_id=document.creator_id,
            created_at=document.created_at.isoformat(),
            file_url=f"https://menoitami/api/documents/{document.id}/file",
            status=document.status,
            type=document.type
        )


# class AddStagesToDocumentSchema(BaseModel):
#     document_id: int
#     stages: list[AddStagesSchema]
