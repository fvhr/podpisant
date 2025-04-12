from sqlalchemy import Integer, String, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship

from database.database import Base


class Organization(Base):
    __tablename__ = 'organization'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    description: Mapped[str] = mapped_column(String(1024), nullable=False)
    admin_id: Mapped[int] = mapped_column(ForeignKey('user.id'))

    admin = relationship("User", back_populates="ad_department", uselist=False)
    departments = relationship("Department", back_populates="department_organizations", uselist=True)
