from pydantic import BaseModel
from sqlalchemy import UUID


class OrganizationBase(BaseModel):
    name: str
    description: str


class OrganizationView(OrganizationBase):
    id: int


class CreateOrganizationSchema(OrganizationBase):
    admin_id: UUID
