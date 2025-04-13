from typing import Optional, List
from uuid import UUID

from pydantic import BaseModel, Field
from sqlalchemy.ext.asyncio import AsyncSession

from api.user.phone_encryptor import encryptor, PhoneEncryptor
from database.models import User


class OrganizationBase(BaseModel):
    name: str
    description: str

class OrganizationView(BaseModel):
    id: int
    name: str
    description: str

class CreateOrganizationSchema(BaseModel):
    name: str
    description: str
    admin_id: UUID | None = None


class DepartmentInfo(BaseModel):
    id: int
    name: str
    is_admin: bool


class DepartmentUserResponse(BaseModel):
    department_id: int
    department_name: str
    is_admin: bool

class FullUserResponse(BaseModel):
    id: UUID
    fio: str
    email: str
    phone: Optional[str]
    telegram_username: Optional[str]
    is_super_admin: bool
    type_notification: Optional[str]
    departments: List[DepartmentUserResponse] = Field(default_factory=list)

    @classmethod
    async def from_db(cls, user: User, encryptor: PhoneEncryptor) -> "FullUserResponse":
        def safe_decrypt(data: str | None) -> str | None:
            if not data:
                return None
            try:
                if len(data) > 32:
                    return encryptor.decrypt(data)
                return data
            except Exception:
                return data

        return cls(
            id=user.id,
            fio=safe_decrypt(user.fio) or "",
            email=user.email,
            phone=safe_decrypt(user.phone),
            telegram_username=user.telegram_username,
            is_super_admin=user.is_super_admin,
            type_notification=user.type_notification.value if user.type_notification else None,
            departments=[
                DepartmentUserResponse(
                    department_id=ud.department_id,
                    department_name=ud.department.name if ud.department else f"Департамент {ud.department_id}",
                    is_admin=ud.is_admin
                )
                for ud in user.user_departments
            ]
        )

# class OrganizationUserView(BaseModel):
#     id: UUID
#     fio: str
#     email: str
#     tags: dict
#     telegram_username:
#     phone: str
#     is_super_admin: bool
