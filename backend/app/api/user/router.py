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
    current_user: User = Depends(get_current_user),
    session: AsyncSession = Depends(get_db)
):
    if not current_user.is_super_admin:
        admin_check = await session.execute(
            select(UserDepartment)
            .where(
                UserDepartment.user_id == current_user.id,
                UserDepartment.department_id == data.department_id,
                UserDepartment.is_admin == True  # Исправлено сравнение
            )
        )
        if not admin_check.scalar_one_or_none():
            raise HTTPException(
                status_code=403,
                detail="Недостаточно прав для добавления пользователя"
            )

        # Проверка на попытку назначить админом департамента
        if data.is_department_admin:
            raise HTTPException(
                status_code=403,
                detail="Только супер-админ может назначать администраторов департамента"
            )

    existing_user = await session.execute(
        select(User)
        .where(User.email == data.email)
    )
    existing_user = existing_user.scalar_one_or_none()

    if existing_user:
        existing_link = await session.execute(
            select(UserDepartment)
            .where(
                UserDepartment.user_id == existing_user.id,
                UserDepartment.department_id == data.department_id
            )
        )

        if existing_link.scalar_one_or_none():
            raise HTTPException(
                status_code=400,
                detail="Пользователь уже состоит в этом департаменте"
            )

        # Создаем новую связь
        user_department = UserDepartment(
            user_id=existing_user.id,
            department_id=data.department_id,
            is_admin=data.is_department_admin
        )
        session.add(user_department)
    else:
        # 4. Если пользователя нет - создаем нового
        new_user = User(
            fio=encryptor.encrypt(data.fio),
            email=data.email,
            telegram_username=data.telegram_username,
            phone=encryptor.encrypt(data.phone),
            type_notification=data.type_notification
        )
        session.add(new_user)
        await session.flush()  # Получаем ID нового пользователя

        user_department = UserDepartment(
            user_id=new_user.id,
            department_id=data.department_id,
            is_admin=data.is_department_admin
        )
        session.add(user_department)

    await session.commit()
    user = existing_user if existing_user else new_user
    return await get_user_by_id(user.id, session)


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
    await session.commit()
