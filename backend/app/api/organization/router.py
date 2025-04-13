from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from api.organization.schemas import CreateOrganizationSchema, UserResponse
from api.user.phone_encryptor import encryptor
from database.models import Organization, organization, UserOrganization, User, UserDepartment
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


@organization_router.get("/{organization_id}/users", response_model=list[UserResponse])
async def get_users_by_organization_id(
    organization_id: int,
    session: AsyncSession = Depends(get_db)
) -> list[UserResponse]:
    org = await session.execute(
        select(Organization)
        .where(Organization.id == organization_id)
        .options(
            selectinload(Organization.users)
            .selectinload(UserOrganization.user)
            .selectinload(User.user_departments)
            .selectinload(UserDepartment.department)
        )
    )
    org = org.scalars().first()

    if not org:
        raise HTTPException(status_code=404, detail="Organization not found")

    users_response = []
    for user_org in org.users:
        user = user_org.user
        if user:
            users_response.append(await UserResponse.from_db(user))

    return users_response
