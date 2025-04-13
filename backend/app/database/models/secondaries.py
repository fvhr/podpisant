from datetime import datetime
from enum import StrEnum
from uuid import UUID

from sqlalchemy import ForeignKey, String, DateTime, func, Enum, BOOLEAN
from sqlalchemy.dialects.postgresql import JSONB
from sqlalchemy.orm import Mapped, relationship, mapped_column
from database.models import Base


class UserDepartment(Base):
    __tablename__ = 'user_department'

    user_id: Mapped[UUID] = mapped_column(ForeignKey('user.id'), primary_key=True)
    department_id: Mapped[int] = mapped_column(ForeignKey('department.id'), primary_key=True)
    is_admin: Mapped[bool] = mapped_column(BOOLEAN, default=False)

    user = relationship("User", back_populates="user_departments")
    department = relationship("Department", back_populates="users")


class StageSigner(Base):
    __tablename__ = "stage_signer"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    stage_id: Mapped[int] = mapped_column(ForeignKey("doc_sign_stage.id"))
    user_id: Mapped[UUID] = mapped_column(ForeignKey("user.id"))
    digital_signature: Mapped[str] = mapped_column(String(512), nullable=True)

    signature_type: Mapped[str] = mapped_column(String(50), nullable=True)
    signed_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=True)

    stage = relationship("DocSignStage", back_populates="signers")
    user = relationship("User", back_populates="signatures")


class UserOrganization(Base):
    __tablename__ = "user_organization"

    user_id: Mapped[UUID] = mapped_column(ForeignKey("user.id"), primary_key=True)
    organization_id: Mapped[int] = mapped_column(ForeignKey("organization.id"), primary_key=True)
    tags: Mapped[dict] = mapped_column(JSONB, nullable=True)

    user = relationship("User", back_populates="organizations")
    organization = relationship("Organization", back_populates="users")
