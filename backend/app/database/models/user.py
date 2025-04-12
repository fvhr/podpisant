import uuid
from enum import StrEnum
from uuid import UUID

from sqlalchemy import String, Boolean
from sqlalchemy.dialects.postgresql import ENUM as PgEnum
from sqlalchemy.orm import mapped_column, Mapped, relationship

from database.models.base import Base


class TypeNotificationEnum(StrEnum):
    TG = "TG"
    EMAIL = "EMAIL"
    PHONE = "PHONE"


class User(Base):
    __tablename__ = 'user'

    id: Mapped[UUID] = mapped_column(primary_key=True, default=uuid.uuid4)
    fio: Mapped[str] = mapped_column(String(128), nullable=False)
    email: Mapped[str] = mapped_column(String(128), nullable=False, unique=True)
    telegram_username: Mapped[str] = mapped_column(String, nullable=True)
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
