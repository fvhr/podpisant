from uuid import UUID

from sqlalchemy import Integer, String, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship

from database.models import Base


class Organization(Base):
    __tablename__ = 'organization'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    description: Mapped[str] = mapped_column(String(1024), nullable=False)
    admin_id: Mapped[UUID] = mapped_column(ForeignKey('user.id'), nullable=False)

    users = relationship("UserOrganization", back_populates="organization",
        cascade="all, delete-orphan",  # Добавьте это
        passive_deletes=True)
    org_documents = relationship("Document", back_populates="organization", cascade="all, delete-orphan")
    departments = relationship("Department", back_populates="organization", cascade="all, delete-orphan")
