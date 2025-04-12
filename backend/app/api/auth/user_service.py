from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app_errors.user_errors import UserNotFoundByEmailError
from database.models.user import User


async def get_user_by_email(session: AsyncSession, user_email: str) -> User:
    user_query = select(User).where(User.email == user_email)
    user = (await session.execute(user_query)).scalar()
    if not user:
        raise UserNotFoundByEmailError(user_email)
    return user
