from pydantic import BaseModel
from sqlalchemy import UUID

class OrganizationBase(BaseModel):
    name: str
    description: str

class OrganizationView(BaseModel):
    id: int
    name: str
    description: str

class OrganizationCreateSchema(BaseModel):
    name: str
    description: str
    admin_id: UUID
