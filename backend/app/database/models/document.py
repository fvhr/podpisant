from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import mapped_column, relationship, Mapped

from database.database import Base


class Document(Base):
    __tablename__ = 'document'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    department_id: Mapped[int] = mapped_column(ForeignKey('department.id'))
    creator_id: Mapped[int] = mapped_column(ForeignKey('user.id'))

    creator = relationship("User", back_populates="documents_created", uselist=False)
    department_document = relationship("Department", back_populates="dep_documents", uselist=False)
    users_document = relationship("User", back_populates="documents", uselist=True)
