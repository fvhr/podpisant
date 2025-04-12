from enum import StrEnum
from uuid import UUID

from sqlalchemy import String, Integer, Boolean
from sqlalchemy.orm import mapped_column, Mapped, relationship
from sqlalchemy.dialects.postgresql import ENUM as PgEnum, JSONB

from database.models.base import Base


class TypeNotificationEnum(StrEnum):
    TG = "TG"
    EMAIL = "EMAIL"
    PHONE = "PHONE"


class User(Base):
    __tablename__ = 'user'

    id: Mapped[UUID] = mapped_column(primary_key=True)
    fio: Mapped[str] = mapped_column(String(128), nullable=False)
    email: Mapped[str] = mapped_column(String(128), nullable=False, unique=True)
    telegram_id: Mapped[int] = mapped_column(Integer, nullable=True)
    phone: Mapped[str] = mapped_column(String(100), nullable=True)
    is_super_admin: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)
    type_notification: Mapped[TypeNotificationEnum | None] = mapped_column(
        PgEnum(TypeNotificationEnum, name="notification_type_enum"),
        nullable=True
    )

    documents_created = relationship("Document", back_populates="creator")
    signatures = relationship("StageSigner", back_populates="user")
    user_departments = relationship("UserDepartment", back_populates="user")
    organizations = relationship("UserOrganization", back_populates="user")
