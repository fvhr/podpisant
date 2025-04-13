from datetime import datetime
from uuid import UUID

from pydantic import BaseModel
from sqlalchemy.ext.asyncio import AsyncSession

from database.models import DocSignStage
from database.models.document import DocumentTypeEnum, DocSignStatus, Document


class CreateDocumentSchema(BaseModel):
    name: str
    organization_id: int


#     stages: list[StageSchema]
#

class AddStagesToDocumentSchema(BaseModel):
    name: str
    number: int
    deadline: datetime | None = None
    user_ids: list[UUID] | None = None

    class Config:
        json_encoders = {
            datetime: lambda v: v.isoformat() if v else None
        }

class DocSignStageCreateSchema(BaseModel):
    id: int
    name: str
    number: int
    deadline: datetime | None
    is_current: bool
    user_ids: list[UUID] | None
    created_at: datetime

    @classmethod
    async def from_db(cls, doc_sign_stage: DocSignStage, session: AsyncSession) -> "DocSignStageCreateSchema":
        # Явно загружаем связанных подписантов
        await session.refresh(doc_sign_stage, ['signers'])

        return cls(
            id=doc_sign_stage.id,
            name=doc_sign_stage.name,
            number=doc_sign_stage.stage_number,
            deadline=doc_sign_stage.deadline,
            is_current=doc_sign_stage.is_current,
            user_ids=[signer.user_id for signer in doc_sign_stage.signers] if doc_sign_stage.signers else None,
            created_at=doc_sign_stage.created_at
        )



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
