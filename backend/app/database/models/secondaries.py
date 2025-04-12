from sqlalchemy.orm import Mapped


class UserDepartment(Base):
    __tablename__ = 'user_department'

    user_id: Mapped[UUID] = mapped_column(ForeignKey('user.id'), primary_key=True)
    department_id: Mapped[int] = mapped_column(ForeignKey('department.id'), primary_key=True)
    role: Mapped[str] = mapped_column(String(50))  # например, "employee", "manager"

    user = relationship("User", back_populates="departments")
    department = relationship("Department", back_populates="users")


class DocumentSignature(Base):
    __tablename__ = 'document_signature'

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    document_id: Mapped[int] = mapped_column(ForeignKey('document.id'))
    user_id: Mapped[UUID] = mapped_column(ForeignKey('user.id'))
    signature_type: Mapped[str] = mapped_column(String(50))  # "digital", "handwritten", etc.
    signed_at: Mapped[datetime] = mapped_column(default=datetime.utcnow)

    document = relationship("Document", back_populates="signatures")
    user = relationship("User")