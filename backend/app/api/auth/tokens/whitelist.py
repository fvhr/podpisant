import logging
from datetime import datetime
from uuid import UUID, uuid4
from typing import Optional

from redis.asyncio import Redis

from api.auth.tokens.types import RefreshTokenWithData, RefreshTokenData

logger = logging.getLogger(__name__)


class TokenWhiteListService:
    def __init__(self, redis: Redis):
        self.redis = redis

    def _serialize_token_data(self, token_data: RefreshTokenWithData) -> dict[str, str]:
        return {
            "jti": token_data.jti,
            "user_id": str(token_data.user_id),
            "created_at": token_data.created_at.isoformat()
        }

    async def store_refresh_token(self, token_data: RefreshTokenWithData, limit: int = 5):
        """Сохраняет refresh token с автоматическим удалением старых"""
        user_tokens_key = f"refresh_tokens:{token_data.user_id}"

        if await self.redis.zcard(user_tokens_key) >= limit:
            oldest_tokens = await self.redis.zrange(user_tokens_key, 0, 0)
            if oldest_tokens:
                oldest_token = oldest_tokens[0]
                oldest_token_str = oldest_token.decode('utf-8') if isinstance(oldest_token, bytes) else str(
                    oldest_token)
                await self.remove_token_by_jti(UUID(oldest_token_str))

        await self.redis.hset(
            f"refresh_token:{token_data.jti}",
            mapping=self._serialize_token_data(token_data)
        )

        await self.redis.zadd(
            user_tokens_key,
            {str(token_data.jti): token_data.created_at.timestamp()}
        )

    async def remove_token_by_jti(self, jti: UUID):
        """Удаляет токен по его идентификатору"""
        jti_str = str(jti)
        jti_bytes = jti_str.encode('utf-8')

        token_data = await self.get_refresh_token_data(jti)
        if not token_data:
            logger.warning(f"Token with jti {jti} not found for deletion")
            return

        # Удаляем из хэша
        await self.redis.delete(f"refresh_token:{jti_str}")

        # Удаляем из сортированного множества
        user_tokens_key = f"refresh_tokens:{token_data.user_id}"
        await self.redis.zrem(user_tokens_key, jti_bytes)


    async def get_refresh_token_data(self, jti: UUID) -> Optional[RefreshTokenData]:
        token_data = await self.redis.hgetall(f"refresh_token:{jti}")
        if not token_data:
            return None

        decoded_data = {
            key.decode('utf-8'): value.decode('utf-8')
            for key, value in token_data.items()
        }

        return RefreshTokenData(
            jti=decoded_data['jti'],
            user_id=UUID(decoded_data['user_id']),
            created_at=datetime.fromisoformat(decoded_data['created_at'])
        )

    async def remove_token_by_jti(self, jti: UUID):
        """Удаляет токен по его идентификатору"""
        token_data = await self.get_refresh_token_data(jti)
        if not token_data:
            logger.warning(f"Token with jti {jti} not found for deletion")
            return

        await self.redis.delete(f"refresh_token:{jti}")
        await self.redis.zrem(f"refresh_tokens:{token_data.user_id}", str(jti))

    async def remove_all_tokens_except(self, user_id: UUID, exclude_jti: UUID):
        tokens = await self.redis.zrange(f"refresh_tokens:{user_id}", 0, -1)
        tokens_to_remove = [t for t in tokens if t != str(exclude_jti)]

        if tokens_to_remove:
            await self.redis.zrem(f"refresh_tokens:{user_id}", *tokens_to_remove)
            for token in tokens_to_remove:
                await self.redis.delete(f"refresh_token:{token}")

