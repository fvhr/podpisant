import uuid
from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from api.auth.idp import get_current_user
from api.auth.schema import UserView
from api.auth.user_service import get_user_orgs_with_admin_and_tags
from api.user.phone_encryptor import encryptor
from api.user.schema import UserDepartament
from app_errors.user_errors import UserNotFoundByIdError, UserNotSuperAdmin
from database.models import UserDepartment, User
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
        fio=encryptor.decrypt(user.fio),
        email=user.email,
        phone=encryptor.decrypt(user.phone) if user.phone else "",
        type_notification=user.type_notification,
        user_organizations=user_organizations,
        admin_in_organization=admin_in_organizations[0] if admin_in_organizations else None,
        is_super_admin=user.is_super_admin,
        user_departments_ids=user_departments_ids,
        organization_tags=org_tags
    )
    return response


@user_router.post("/user/departament")
async def user_departament(
        data: UserDepartament,
        user: User = Depends(get_current_user),
        session: AsyncSession = Depends(get_db)):
    if not user.is_super_admin:
        result = await session.execute(
            select(UserDepartment).where(
                UserDepartment.user_id == user.id,
                UserDepartment.department_id == data.dep_id,
                UserDepartment.is_admin is True
            )
        )
        user_dep = result.scalar_one_or_none()
        if not user_dep:
            raise UserNotSuperAdmin
        if data.is_dep_admin:
            raise HTTPException(status_code=403, detail="Вы не можете назначать других пользователей администраторами")
    user_uuid = uuid.uuid4()
    new_user = User(
        id=user_uuid,
        fio=encryptor.encrypt(data.fio),
        email=data.email,
        telegram_username=data.telegram_username,
        phone=encryptor.encrypt(data.phone),
        type_notification=data.type_notification
    )
    dep_us = UserDepartment(
        user_id=new_user.id,
        department_id=data.dep_id,
        is_admin=data.is_dep_admin
    )

    try:
        session.add(new_user)
        session.add(dep_us)
        await session.commit()
        return await get_user_by_id(new_user.id, session)
    except Exception as ex:
        print(ex)
        raise HTTPException(detail='Internal server error', status_code=500)


@user_router.delete("/{user_uuid}/{dep_id}")
async def user_departament(
        user_uuid: str,
        dep_id: int,
        user: User = Depends(get_current_user),
        session: AsyncSession = Depends(get_db)):
    if not user.is_super_admin:
        result = await session.execute(
            select(UserDepartment).where(
                UserDepartment.user_id == user.id,
                UserDepartment.department_id == dep_id,
                UserDepartment.is_admin is True
            )
        )
        user_dep = result.scalar_one_or_none()
        if not user_dep:
            raise UserNotSuperAdmin
    del_user = await session.get(User, user_uuid)
    query = select(UserDepartment).where(UserDepartment.department_id == dep_id,
                                         UserDepartment.user_id == user_uuid)
    del_deps = await session.execute(query)
    del_deps = del_deps.scalar_one_or_none()
    if del_deps:
        await session.delete(del_deps)
    await session.delete(del_user)
