from pydantic import BaseModel


class OrganizationView(BaseModel):
    id: int
    name: str
    description: str