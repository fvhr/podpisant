from datetime import datetime

from sqlalchemy import Integer, ForeignKey, DateTime, String, func, Boolean
from sqlalchemy.orm import mapped_column, Mapped, relationship

from database.models import Base


class DocSignStage(Base):
    __tablename__ = "doc_sign_stage"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    doc_id: Mapped[int] = mapped_column(ForeignKey("document.id"))
    stage_number: Mapped[int] = mapped_column(Integer, nullable=False, default=1)
    name: Mapped[str] = mapped_column(String(100), nullable=False)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=func.now())
    deadline: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=True)
    is_current: Mapped[bool] = mapped_column(Boolean, default=False)

    document = relationship(
        "Document",
        back_populates="stages",
        foreign_keys=[doc_id]
    )
    signers = relationship(
        "StageSigner",
        back_populates="stage",
        foreign_keys="StageSigner.stage_id"
    )
