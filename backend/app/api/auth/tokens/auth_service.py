import logging
from typing import Annotated, NewType, TypedDict
from uuid import UUID, uuid4

from fastapi import Depends
from redis.asyncio import Redis

from api.auth.tokens.config import JWTSettings
from api.auth.tokens.tokens import AuthServerTokenCreationService, JWTService
from api.auth.tokens.types import RefreshToken, FullAccessTokenPayload, AccessToken
from api.auth.tokens.whitelist import TokenWhiteListService
from app_errors.application_errors import DeviceMismatchException
from database.models.user import User
from myredis.connection import get_redis

logger = logging.getLogger(__name__)


DeviceId = NewType("DeviceId", str)


class Tokens(TypedDict, total=False):
    access_token: AccessToken
    refresh_token: RefreshToken
    device_id: DeviceId  # Добавить это поле


class HttpAuthServerService:
    """Сервис для аутентификации, обновления и управления токенами."""

    def __init__(
        self,
        jwt_service: JWTService,
        token_creation_service: AuthServerTokenCreationService,
        token_whitelist_service: TokenWhiteListService,
        jwt_settings: JWTSettings,
        device_id: DeviceId | None = None,
    ):
        self.jwt_service = jwt_service
        self.token_creation_service = token_creation_service
        self.token_whitelist_service = token_whitelist_service
        self.jwt_settings = jwt_settings
        self.device_id = device_id or str(uuid4())

    def _get_token_jti(self, refresh_token: RefreshToken) -> UUID:
        """Извлекает JTI из refresh токена."""
        payload = self.jwt_service.decode(refresh_token)
        return payload["jti"]

    async def revoke(self, refresh_token: RefreshToken) -> None:
        """Отзывает указанный refresh токен."""
        jti = self._get_token_jti(refresh_token)
        await self.token_whitelist_service.remove_token_by_jti(jti)

    async def invalidate_other_tokens(
        self, refresh_token: RefreshToken, device_id: DeviceId
    ) -> None:
        """
        Инвалидирует все токены для пользователя, кроме текущего,
        с проверкой соответствия device_id.
        """
        payload: FullAccessTokenPayload = self.jwt_service.decode(refresh_token)
        jti = payload["jti"]

        if not await self.token_whitelist_service.is_device_valid(jti, str(device_id)):
            raise DeviceMismatchException()

        user_id: UUID = payload["sub"]  # type: ignore
        await self.token_whitelist_service.remove_all_tokens_except(user_id, jti)

    async def create_and_save_tokens(
        self,
        user: User,
        device_id: DeviceId | None = None,
    ) -> tuple[AccessToken, RefreshToken]:
        """
        Создаёт и сохраняет пару токенов (access + refresh).

        :param user: Пользователь
        :param device_id: Идентификатор устройства (если None - будет сгенерирован)
        :param is_admin: Флаг администратора
        :return: Пара токенов
        """
        current_device_id = device_id or self.device_id
        user_id: UUID = user.id

        # Создаем access токен
        access_token = self.token_creation_service.create_auth_server_access_token(
            user_id, user.is_super_admin
        )

        # Создаем и сохраняем refresh токен
        refresh_token_data = await self.token_creation_service.create_auth_server_refresh_token(
            user_id, current_device_id
        )

        await self.token_whitelist_service.store_refresh_token(
            refresh_token_data,
            self.jwt_settings.refresh_token_by_user_limit,
        )

        return access_token, refresh_token_data.token

async def get_auth_service(
    redis: Redis = Depends(get_redis),
) -> HttpAuthServerService:
    """Фабрика для создания HttpAuthServerService с зависимостями."""

    jwt_settings = JWTSettings()

    jwt_service = JWTService(jwt_settings)

    token_whitelist_service = TokenWhiteListService(redis)

    token_creation_service = AuthServerTokenCreationService(
        jwt_settings=jwt_settings,
        jwt_service=jwt_service
    )

    auth_service = HttpAuthServerService(
        jwt_service=jwt_service,
        token_creation_service=token_creation_service,
        token_whitelist_service=token_whitelist_service,
        jwt_settings=jwt_settings
    )

    return auth_service
