from typing import Annotated

from click import UUID
from fastapi import Depends, Request, HTTPException
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from sqlalchemy.ext.asyncio import AsyncSession
from starlette import status

from api.auth.tokens.auth_service import get_whitelist_service, get_jwt_service
from api.auth.tokens.tokens import JWTService
from api.auth.tokens.whitelist import TokenWhiteListService
from app_errors.user_errors import UserNotFoundByIdError
from database.models import User
from database.session_manager import get_db


async def get_refresh_token(
    request: Request,
    credentials: HTTPAuthorizationCredentials = Depends(HTTPBearer(auto_error=False))
) -> str:
    """
    Извлекает refresh_token:
    1. Сначала проверяет Authorization header (Bearer token)
    2. Если нет - проверяет куки
    3. Если нет нигде - возвращает 401 ошибку
    """
    # Проверяем Authorization header
    if credentials:
        return credentials.credentials

    # Проверяем куки
    refresh_token = request.cookies.get("refresh_token")
    if refresh_token:
        return refresh_token

    # Если токен не найден
    raise HTTPException(
        status_code=401,
        detail="Refresh token is missing",
        headers={"WWW-Authenticate": "Bearer"},
    )


async def get_current_user(
    request: Request,
    credentials: Annotated[HTTPAuthorizationCredentials, Depends(HTTPBearer())],
    token_whitelist: TokenWhiteListService = Depends(get_whitelist_service),
    session: AsyncSession = Depends(get_db),
    jwt_service: JWTService = Depends(get_jwt_service),
) -> User:
    access_token = credentials.credentials
    access_payload = jwt_service.decode(access_token)
    user_id = UUID(access_payload["sub"])

    refresh_token = request.cookies.get("refresh_token")
    if not refresh_token:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Refresh token missing"
        )

    refresh_payload = jwt_service.decode(refresh_token)
    refresh_jti = UUID(refresh_payload["jti"])

    token_data = await token_whitelist.get_refresh_token_data(refresh_jti)
    if not token_data or token_data.user_id != user_id:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Refresh token revoked"
        )

    user = await session.get(User, user_id)
    if not user:
        raise UserNotFoundByIdError(user_id)

    return user
