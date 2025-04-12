import logging
from datetime import datetime
from uuid import UUID, uuid4
from typing import Optional, Union

from redis.asyncio import Redis

from api.auth.tokens.types import RefreshTokenData, RefreshTokenWithData

logger = logging.getLogger(__name__)


class TokenWhiteListService:
    def __init__(self, redis: Redis):
        self.redis = redis

    def _ensure_str(self, value: Union[str, bytes]) -> str:
        """Универсальное преобразование в строку"""
        return value.decode('utf-8') if isinstance(value, bytes) else str(value)

    def _serialize_token_data(self, token_data: RefreshTokenWithData) -> dict[str, str]:
        return {
            "jti": str(token_data.jti),
            "user_id": str(token_data.user_id),
            "created_at": token_data.created_at.isoformat()
        }

    async def store_refresh_token(self, token_data: RefreshTokenWithData, limit: int = 5):
        """Сохраняет refresh token с автоматическим удалением старых"""
        user_tokens_key = f"refresh_tokens:{token_data.user_id}"

        if await self.redis.zcard(user_tokens_key) >= limit:
            oldest_tokens = await self.redis.zrange(user_tokens_key, 0, 0)
            if oldest_tokens:
                oldest_token = self._ensure_str(oldest_tokens[0])
                try:
                    await self.remove_token_by_jti(UUID(oldest_token))
                except (ValueError, AttributeError) as e:
                    logger.error(f"Failed to remove old token: {e}")

        await self.redis.hset(
            f"refresh_token:{token_data.jti}",
            mapping=self._serialize_token_data(token_data)
        )

        await self.redis.zadd(
            user_tokens_key,
            {str(token_data.jti): token_data.created_at.timestamp()}
        )

    async def remove_token_by_jti(self, jti: UUID):
        """Универсальное удаление токена"""
        try:
            jti_str = str(jti)
            token_data = await self.get_refresh_token_data(jti)
            if not token_data:
                return

            # Удаление из всех структур
            await self.redis.delete(f"refresh_token:{jti_str}")
            await self.redis.zrem(
                f"refresh_tokens:{token_data.user_id}",
                jti_str.encode('utf-8')  # Работает для обоих случаев
            )
        except Exception as e:
            logger.error(f"Error removing token {jti}: {e}")
            raise

    async def get_refresh_token_data(self, jti: UUID) -> Optional[RefreshTokenData]:
        """Универсальное получение данных токена"""
        try:
            token_data = await self.redis.hgetall(f"refresh_token:{jti}")
            if not token_data:
                return None

            decoded = {
                self._ensure_str(k): self._ensure_str(v)
                for k, v in token_data.items()
            }

            return RefreshTokenData(
                jti=decoded['jti'],
                user_id=UUID(decoded['user_id']),
                created_at=datetime.fromisoformat(decoded['created_at'])
            )
        except Exception as e:
            logger.error(f"Error getting token data: {e}")
            return None

    async def remove_all_tokens_except(self, user_id: UUID, exclude_jti: UUID):
        """Универсальное удаление всех токенов кроме указанного"""
        try:
            tokens = await self.redis.zrange(f"refresh_tokens:{user_id}", 0, -1)
            tokens_to_remove = [
                t for t in tokens
                if self._ensure_str(t) != str(exclude_jti)
            ]

            if tokens_to_remove:
                tokens_bytes = [t.encode('utf-8') if isinstance(t, str) else t for t in tokens_to_remove]
                await self.redis.zrem(f"refresh_tokens:{user_id}", *tokens_bytes)

                for token in tokens_to_remove:
                    await self.redis.delete(f"refresh_token:{self._ensure_str(token)}")
        except Exception as e:
            logger.error(f"Error removing tokens: {e}")
            raise
