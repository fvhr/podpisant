from pydantic import BaseModel

from database.models.user import TypeNotificationEnum


class UserDepartament(BaseModel):
    dep_id: int
    fio: str

    email: str | None
    phone: str | None
    telegram_username: str | None
    type_notification: TypeNotificationEnum
    is_dep_admin: bool
