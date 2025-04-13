from uuid import UUID

from pydantic import BaseModel

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


# class OrganizationUserView(UserView):
#     department_ids: list[int] = []
#     department_names: list[str] = []


# class OrganizationUserView(BaseModel):
#     id: UUID
#     fio: str
#     email: str
#     tags: dict
#     telegram_username:
#     phone: str
#     is_super_admin: bool
