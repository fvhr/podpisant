from typing import Type
from uuid import UUID

from sqlalchemy import select, case
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import aliased

from app_errors.user_errors import UserNotFoundByEmailError, UserNotFoundByIdError
from database.models import User, Organization, UserOrganization
from database.models.user import User


async def get_user_by_email(session: AsyncSession, user_email: str) -> User:
    user_query = select(User).where(User.email == user_email)
    user = (await session.execute(user_query)).scalar()
    if not user:
        raise UserNotFoundByEmailError(user_email)
    return user


async def get_user_by_id(session: AsyncSession, user_id: UUID) -> User:
    user = await session.get(User, user_id)
    if not user:
        raise UserNotFoundByIdError(str(user_id))
    return user


async def get_user_orgs_with_admin_and_tags(session, user_id: UUID):
    orgs_query = select(
        Organization.id,
        (Organization.admin_id == user_id).label("is_admin"),
        UserOrganization.tags
    ).join(
        UserOrganization,
        UserOrganization.organization_id == Organization.id
    ).where(
        UserOrganization.user_id == user_id
    )

    result = await session.execute(orgs_query)
    rows = result.all()

    all_org_ids = []
    admin_org_ids = []
    org_tags = {}

    for org_id, is_admin, tags in rows:
        all_org_ids.append(org_id)
        if is_admin:
            admin_org_ids.append(org_id)
        if tags:  # Добавляем теги, если они есть
            org_tags[org_id] = tags

    return all_org_ids, admin_org_ids, org_tags
