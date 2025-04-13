from uuid import UUID

from pydantic import BaseModel, Field

from api.user.phone_encryptor import encryptor
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


class UserResponse(BaseModel):
    id: UUID
    fio: str
    email: str
    phone: str | None = None
    telegram_username: str | None = None
    is_super_admin: bool
    type_notification: str | None = None
    departments: list[str] = Field(default_factory=list)  # Исправлено здесь

    @classmethod
    async def from_db(cls, user: User) -> "UserResponse":
        try:
            # Дешифруем только если данные похожи на зашифрованные
            phone = user.phone
            if phone and len(phone) > 32:  # Примерная проверка на зашифрованные данные
                phone = encryptor.decrypt(phone)

            fio = user.fio
            if fio and len(fio) > 32:
                fio = encryptor.decrypt(fio)

            return cls(
                id=user.id,
                fio=fio or "",
                email=user.email,
                phone=phone,
                telegram_username=user.telegram_username,
                is_super_admin=user.is_super_admin,
                type_notification=user.type_notification.value if user.type_notification else None,
                departments=[ud.department.name for ud in user.user_departments if ud.department]
            )
        except Exception as e:
            # Логируем ошибку, но возвращаем данные без дешифровки
            print(f"Decryption error for user {user.id}: {str(e)}")
            return cls(
                id=user.id,
                fio=user.fio or "",
                email=user.email,
                phone=user.phone,
                telegram_username=user.telegram_username,
                is_super_admin=user.is_super_admin,
                type_notification=user.type_notification.value if user.type_notification else None,
                departments=[ud.department.name for ud in user.user_departments if ud.department]
            )


# class OrganizationUserView(BaseModel):
#     id: UUID
#     fio: str
#     email: str
#     tags: dict
#     telegram_username:
#     phone: str
#     is_super_admin: bool
