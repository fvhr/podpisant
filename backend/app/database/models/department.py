from datetime import datetime

from sqlalchemy import Integer, String, ForeignKey, DateTime
from sqlalchemy.orm import Mapped, mapped_column, relationship

from database.models import Base


class Department(Base):
    __tablename__ = 'department'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(255), unique=True)
    desc: Mapped[str] = mapped_column(String(1024), nullable=True)
    organization_id: Mapped[int] = mapped_column(ForeignKey('organization.id'))
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)

    users = relationship("UserDepartment", back_populates="department")
    organization = relationship("Organization", back_populates="departments")
