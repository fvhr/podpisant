from typing import Type
from uuid import UUID

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app_errors.user_errors import UserNotFoundByEmailError, UserNotFoundByIdError
from database.models import User
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
