from datetime import datetime
from enum import StrEnum

from sqlalchemy import Column, Integer, String, ForeignKey, DateTime
from sqlalchemy.orm import mapped_column, relationship, Mapped
from sqlalchemy.dialects.postgresql import ENUM as PgEnum

from database.models import Base


class DocumentTypeEnum(StrEnum):
    STRICT = 'strict'
    GROUP = 'group'


class DocSignStatus(StrEnum):
    IN_PROGRESS = 'В процессе'
    SIGNED = 'Подписан'
    REJECTED = 'Отклонен'


class Document(Base):
    __tablename__ = 'document'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    organization_id: Mapped[int] = mapped_column(ForeignKey('organization.id'))
    creator_id: Mapped[int] = mapped_column(ForeignKey('user.id'))
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)
    status: Mapped[DocSignStatus] = mapped_column(PgEnum(DocSignStatus), default=DocSignStatus.IN_PROGRESS)
    type: Mapped[DocumentTypeEnum | None] = mapped_column(
        PgEnum(DocumentTypeEnum),
        default=None,
        nullable=True
    )

    stages = relationship("DocSignStage", back_populates="document", order_by="DocSignStage.stage_number")
    creator = relationship("User", back_populates="documents_created", uselist=False)
    organization = relationship("Organization", back_populates="org_documents")
