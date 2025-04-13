from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession

from api.organization.schemas import CreateOrganizationSchema
from database.models import Organization, organization
from database.session_manager import get_db

organization_router = APIRouter(prefix="/organizations", tags=["organizations"])

@organization_router.post("")
async def create_organization(organization_data: CreateOrganizationSchema, session: AsyncSession = Depends(get_db)):
    organization = Organization(**organization_data.model_dump())
    organization = await session.merge(organization)
    await session.commit()
    return {"oraganization_id": organization.id}


@organization_router.delete("/{organization_id}")
async def delete_organization(organization_id: int, session: AsyncSession = Depends(get_db)):
    await session.delete(organization)
    await session.commit()


@organization_router.get("/{organization_id}/users")
async def get_users_by_organization_id(organization_id: int, session: AsyncSession = Depends(get_db)):

