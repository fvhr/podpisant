from uuid import UUID

from fastapi import APIRouter, Depends
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from api.auth.schema import UserView
from api.auth.user_service import get_user_orgs_with_admin_and_tags
from app_errors.user_errors import UserNotFoundByIdError
from database.models import UserDepartment, user, User
from database.session_manager import get_db

user_router = APIRouter(prefix="/users", tags=["users"])

@user_router.get("/{user_id}")
async def get_user_by_id(user_id: UUID, session: AsyncSession = Depends(get_db)) -> UserView:
    user = await session.get(User, user_id)
    if not user:
        raise UserNotFoundByIdError
    user_organizations, admin_in_organizations, org_tags = await get_user_orgs_with_admin_and_tags(session, user_id)

    # Получаем ID департаментов пользователя
    user_departments_ids_query = select(UserDepartment.department_id).where(UserDepartment.user_id == user.id)
    user_departments_ids = (await session.execute(user_departments_ids_query)).scalars().all()

    response = UserView(
        id=user.id,
        fio=user.fio,
        email=user.email,
        phone=user.phone if user.phone else "",
        type_notification=user.type_notification,
        user_organizations=user_organizations,
        admin_in_organization=admin_in_organizations[0] if admin_in_organizations else None,
        is_super_admin=user.is_super_admin,
        user_departments_ids=user_departments_ids,
        organization_tags=org_tags
    )
    return response


