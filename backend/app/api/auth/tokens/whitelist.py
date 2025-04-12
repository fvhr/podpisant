import logging
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
        # Удаляем старые токены при превышении лимита
        user_tokens_key = f"refresh_tokens:{token_data.user_id}"
        if await self.redis.zcard(user_tokens_key) >= limit:
            oldest_tokens = await self.redis.zrange(user_tokens_key, 0, 0)
            if oldest_tokens:
                await self.remove_token_by_jti(UUID(oldest_tokens[0]))

        # Сохраняем новый токен
        await self.redis.hset(
            f"refresh_token:{token_data.jti}",
            mapping=self._serialize_token_data(token_data)
        )
        await self.redis.zadd(
            user_tokens_key,
            {token_data.jti: token_data.created_at.timestamp()}
        )

    async def get_refresh_token_data(self, jti: UUID) -> Optional[RefreshTokenData]:
        token_data = await self.redis.hgetall(f"refresh_token:{jti}")
        return RefreshTokenData(**token_data) if token_data else None

    async def remove_token_by_jti(self, jti: UUID):
        token_data = await self.get_refresh_token_data(jti)
        if not token_data:
            return

        await self.redis.delete(f"refresh_token:{jti}")
        await self.redis.zrem(f"refresh_tokens:{token_data.user_id}", jti)

    async def remove_all_tokens_except(self, user_id: UUID, exclude_jti: UUID):
        tokens = await self.redis.zrange(f"refresh_tokens:{user_id}", 0, -1)
        tokens_to_remove = [t for t in tokens if t != str(exclude_jti)]

        if tokens_to_remove:
            await self.redis.zrem(f"refresh_tokens:{user_id}", *tokens_to_remove)
            for token in tokens_to_remove:
                await self.redis.delete(f"refresh_token:{token}")

