from datetime import timedelta, datetime, timezone
from typing import Any
from uuid import UUID, uuid4

from fastapi.responses import ORJSONResponse
import jwt
from pytz import timezone

from api.auth.tokens.config import JWTSettings
from api.auth.tokens.types import AccessToken, RefreshTokenWithData, RefreshToken
from app_errors.application_errors import TokenExpiredError, InvalidTokenError


class JWTService:
    """Реализация сервиса работы с JWT токенами."""

    def __init__(self, auth_settings: JWTSettings):
        self.auth_settings = auth_settings

    def encode(
        self,
        payload: dict[str, int | str | UUID],
        expire_minutes: int | None = None,
        expire_timedelta: timedelta | None = None,
    ) -> dict[str, str | datetime]:
        """Создаёт JWT токен с указанным сроком действия."""
        tz = timezone("Europe/Moscow")
        now = datetime.now(tz)

        if expire_timedelta:
            expire = now + expire_timedelta
        else:
            expire = now + timedelta(
                minutes=expire_minutes
                or self.auth_settings.access_token_expire_minutes
            )
        token = jwt.encode(
            {**payload, "exp": expire, "iat": now},
            self.auth_settings.private_key,
            algorithm=self.auth_settings.algorithm,
        )
        return {
            "token": token,
            "created_at": now,
            "expires_at": expire,
        }

    def decode(self, token: str) -> dict[str, Any]:
        """Декодирует JWT токен и возвращает его payload."""
        try:
            payload = jwt.decode(
                token,
                self.auth_settings.public_key,
                algorithms=[self.auth_settings.algorithm],
            )
            return payload
        except jwt.ExpiredSignatureError:
            raise TokenExpiredError()
        except jwt.DecodeError:
            raise InvalidTokenError()


class AuthServerTokenCreationService:
    """Реализация сервиса создания токенов с использованием JWTService."""

    def __init__(self, jwt_settings: JWTSettings, jwt_service: JWTService):
        self.jwt_service = jwt_service
        self.jwt_settings = jwt_settings

    def create_auth_server_access_token(self, user_id: UUID, is_admin: bool) -> AccessToken:
        payload = {
            "sub": str(user_id),
            "jti": str(uuid4()),
            "is_admin": is_admin
        }
        encoded = self.jwt_service.encode(payload)
        return AccessToken(encoded["token"])

    async def create_auth_server_refresh_token(self, user_id: UUID) -> RefreshTokenWithData:
        jti = str(uuid4())
        payload = {"sub": str(user_id), "jti": jti}
        encoded = self.jwt_service.encode(payload)

        return RefreshTokenWithData(
            token=RefreshToken(encoded["token"]),
            user_id=user_id,
            jti=jti,
            created_at=encoded["created_at"]
        )


def set_tokens(response: ORJSONResponse, access_token: str, refresh_token: str):
    response.set_cookie(
        key="refresh_token",
        value=refresh_token,
        httponly=True,
        secure=False,
        max_age=60 * 60 * 24 * 30,
        expires=60 * 60 * 24 * 30,
        samesite="none",
    )
    response.set_cookie(
        key="access_token",
        value=access_token,
        httponly=True,
        secure=False,
        max_age=60 * 60 * 24 * 30,
        expires=60 * 60 * 24 * 30,
        samesite="none",
    )