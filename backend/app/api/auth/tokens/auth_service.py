import logging
from typing import NewType, TypedDict
from uuid import UUID

from fastapi import Depends
from redis.asyncio import Redis

from api.auth.tokens.config import JWTSettings
from api.auth.tokens.tokens import AuthServerTokenCreationService, JWTService
from api.auth.tokens.types import RefreshToken, AccessToken
from api.auth.tokens.whitelist import TokenWhiteListService
from database.models.user import User
from myredis.connection import get_redis

logger = logging.getLogger(__name__)


DeviceId = NewType("DeviceId", str)


class Tokens(TypedDict, total=False):
    access_token: AccessToken
    refresh_token: RefreshToken


class HttpAuthServerService:
    def __init__(
        self,
        jwt_service: JWTService,
        token_creation_service: AuthServerTokenCreationService,
        token_whitelist_service: TokenWhiteListService,
        jwt_settings: JWTSettings
    ):
        self.jwt_service = jwt_service
        self.token_creation_service = token_creation_service
        self.token_whitelist_service = token_whitelist_service
        self.jwt_settings = jwt_settings

    async def create_and_save_tokens(self, user: User) -> tuple[AccessToken, RefreshToken]:
        access_token = self.token_creation_service.create_auth_server_access_token(
            user.id, user.is_super_admin
        )

        refresh_token_data = await self.token_creation_service.create_auth_server_refresh_token(
            user.id
        )

        await self.token_whitelist_service.store_refresh_token(
            refresh_token_data,
            self.jwt_settings.refresh_token_by_user_limit
        )

        return access_token, refresh_token_data.token

    async def revoke_token(self, refresh_token: RefreshToken):
        payload = self.jwt_service.decode(refresh_token)
        await self.token_whitelist_service.remove_token_by_jti(UUID(payload["jti"]))

    async def invalidate_other_tokens(self, refresh_token: RefreshToken):
        payload = self.jwt_service.decode(refresh_token)
        await self.token_whitelist_service.remove_all_tokens_except(
            UUID(payload["sub"]),
            UUID(payload["jti"])
        )

def get_jwt_settings() -> JWTSettings:
    return JWTSettings()

def get_whitelist_service(redis: Redis = Depends(get_redis)) -> TokenWhiteListService:
    token_whitelist_service = TokenWhiteListService(redis)
    return token_whitelist_service


def get_jwt_service(jwt_settings: JWTSettings = Depends(get_jwt_settings)) -> JWTService:
    jwt_service = JWTService(jwt_settings)
    return jwt_service

def get_auth_service(
    token_whitelist_service: TokenWhiteListService = Depends(get_whitelist_service),
    jwt_service: JWTService = Depends(get_jwt_service),
    jwt_settings: JWTSettings = Depends(get_jwt_settings)
) -> HttpAuthServerService:
    """Фабрика для создания HttpAuthServerService с зависимостями."""

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
