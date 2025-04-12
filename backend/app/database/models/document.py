from datetime import datetime

from sqlalchemy import Column, Integer, String, ForeignKey, DateTime
from sqlalchemy.orm import mapped_column, relationship, Mapped

from database.db import Base


class Document(Base):
    __tablename__ = 'document'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    department_id: Mapped[int] = mapped_column(ForeignKey('department.id'))
    creator_id: Mapped[int] = mapped_column(ForeignKey('user.id'))
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)

    creator = relationship("User", back_populates="documents_created", uselist=False)
    department_document = relationship("Department", back_populates="dep_documents", uselist=False)
    users_document = relationship("User", back_populates="documents", uselist=True)
