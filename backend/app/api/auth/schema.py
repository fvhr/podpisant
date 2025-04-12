from uuid import UUID

from pydantic import BaseModel

from database.models.user import TypeNotificationEnum


class AuthSchema(BaseModel):
    email: str

class AuthWithCodeSchema(BaseModel):
    code: str
    device_id: str


class AuthCodeSchema(BaseModel):
    code: str


class UserView(BaseModel):
    id: UUID
    email: str
    fio: str
    phone: str | None
    is_super_admin: bool
    type_notification: TypeNotificationEnum | None
    admin_in_organizations: list[int] | None
    user_organizations: list[int] | None
    user_departments_ids: list[int]
