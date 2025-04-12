from enum import StrEnum
from uuid import UUID

from sqlalchemy import String, Integer, Boolean
from sqlalchemy.orm import mapped_column, Mapped
from sqlalchemy.dialects.postgresql import ENUM as PgEnum

from database.models.base import Base


class TypeNotificationEnum(StrEnum):
    TG = "TG"
    EMAIL = "EMAIL"
    PHONE = "PHONE"


class User(Base):
    __tablename__ = 'user'

    id: Mapped[UUID] = mapped_column(primary_key=True)
    fio: Mapped[str] = mapped_column(String(128), nullable=False)
    email: Mapped[str] = mapped_column(String(128), nullable=False)
    telegram_id: Mapped[int] = mapped_column(Integer, nullable=True)
    phone: Mapped[str] = mapped_column(String(20), nullable=True)
    is_super_admin: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)
    type_notification: Mapped[TypeNotificationEnum | None] = mapped_column(
        PgEnum(TypeNotificationEnum, name="notification_type_enum"),
        nullable=True
    )
