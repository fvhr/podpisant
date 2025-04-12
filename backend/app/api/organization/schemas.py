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
    admin_id: UUID
