from typing import List

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from api.organization.schemas import CreateOrganizationSchema, FullUserResponse
from api.user.phone_encryptor import encryptor
from database.models import Organization, organization, UserOrganization, User, UserDepartment, Department
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
    organization = await session.get(Organization, organization_id)
    await session.delete(organization)
    await session.commit()


@organization_router.get("/{org_id}/users", response_model=List[FullUserResponse])
async def get_org_users(
    org_id: int,
    session: AsyncSession = Depends(get_db),
):
    try:
        org_users = (await session.execute(
            select(User)
            .join(UserOrganization, UserOrganization.user_id == User.id)
            .join(Organization, Organization.id == UserOrganization.organization_id)
            .where(Organization.id == org_id)
            .options(
                selectinload(User.user_departments).joinedload(UserDepartment.department),
                selectinload(User.organizations)
            )
        )).scalars().unique().all()

        dept_users = (await session.execute(
            select(User)
            .join(UserDepartment, UserDepartment.user_id == User.id)
            .join(Department, Department.id == UserDepartment.department_id)
            .where(Department.organization_id == org_id)
            .options(
                selectinload(User.user_departments).joinedload(UserDepartment.department),
                selectinload(User.organizations)
            )
        )).scalars().unique().all()

        all_users = {user.id: user for user in org_users + dept_users}.values()

        return [await FullUserResponse.from_db(user, encryptor) for user in all_users]

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
