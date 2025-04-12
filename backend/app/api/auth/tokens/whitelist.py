import logging
from uuid import UUID, uuid4
from typing import Optional

from redis.asyncio import Redis

from api.auth.tokens.types import RefreshTokenWithData, RefreshTokenData

logger = logging.getLogger(__name__)


class TokenWhiteListService:
    """Реализация сервиса управления белым списком токенов с использованием Redis."""

    def __init__(self, redis: Redis):
        self.redis = redis

    def _generate_device_id(self) -> str:
        """Генерирует новый UUID идентификатор устройства."""
        return str(uuid4())

    def _serialize_refresh_token_data(self, refresh_token_data: RefreshTokenWithData) -> dict[str, str]:
        """Сериализует данные токена для хранения в Redis."""
        return {
            "jti": str(refresh_token_data.jti),
            "user_id": str(refresh_token_data.user_id),
            "device_id": str(refresh_token_data.device_id),
            "created_at": refresh_token_data.created_at.isoformat(),
        }

    async def remove_token_by_jti(self, jti: UUID) -> None:
        """Удаляет токен из Redis по его идентификатору."""
        token_data = await self.get_refresh_token_data(jti)
        if not token_data:
            logger.warning("Токен с jti %s не найден для удаления.", jti)
            return

        await self.redis.delete(f"refresh_token:{jti}")
        await self.redis.zrem(f"refresh_tokens:{token_data.user_id}", str(jti))
        logger.info("Удалён токен с jti %s", jti)

    async def is_device_valid(self, jti: UUID, device_id: str) -> bool:
        """
        Проверяет, соответствует ли device_id токену.

        :param jti: Идентификатор токена
        :param device_id: UUID устройства в виде строки
        :return: True если device_id совпадает с сохраненным
        """
        token_data = await self.redis.hgetall(f"refresh_token:{jti}")
        if not token_data:
            logger.warning("Токен с jti %s не найден", jti)
            return False

        stored_device_id = token_data.get("device_id")
        if not stored_device_id:
            logger.warning("Device ID отсутствует в данных токена с jti %s", jti)
            return False

        return stored_device_id == device_id

    async def store_refresh_token(self, refresh_token_data: RefreshTokenWithData, limit: int = 5) -> str:
        """
        Сохраняет refresh токен с автоматической генерацией device_id.

        :param refresh_token_data: Данные токена
        :param limit: Максимальное количество токенов на пользователя
        :return: Сгенерированный device_id
        """
        if not hasattr(refresh_token_data, 'device_id') or not refresh_token_data.device_id:
            device_id = self._generate_device_id()
            refresh_token_data.device_id = UUID(device_id)
        else:
            device_id = str(refresh_token_data.device_id)

        # Удаляем старые токены при превышении лимита
        user_tokens_key = f"refresh_tokens:{refresh_token_data.user_id}"
        if await self.redis.zcard(user_tokens_key) >= limit:
            oldest_tokens = await self.redis.zrange(user_tokens_key, 0, 0)
            if oldest_tokens:
                await self.remove_token_by_jti(UUID(oldest_tokens[0]))

        # Сохраняем новый токен
        await self.redis.hset(
            f"refresh_token:{refresh_token_data.jti}",
            mapping=self._serialize_refresh_token_data(refresh_token_data)
        )
        await self.redis.zadd(
            user_tokens_key,
            {str(refresh_token_data.jti): refresh_token_data.created_at.timestamp()}
        )

        logger.info("Сохранён refresh токен для устройства %s", device_id)
        return device_id

    async def get_refresh_token_data(self, jti: UUID) -> Optional[RefreshTokenData]:
        """Получает данные refresh токена по его идентификатору."""
        token_data = await self.redis.hgetall(f"refresh_token:{jti}")
        return RefreshTokenData(**token_data) if token_data else None

    async def remove_all_tokens_except(self, user_id: UUID, exclude_jti: UUID) -> None:
        """Удаляет все токены пользователя, кроме указанного."""
        tokens = await self.redis.zrange(f"refresh_tokens:{user_id}", 0, -1)
        tokens_to_remove = [t for t in tokens if t != str(exclude_jti)]

        if tokens_to_remove:
            await self.redis.zrem(f"refresh_tokens:{user_id}", *tokens_to_remove)
            for token in tokens_to_remove:
                await self.redis.delete(f"refresh_token:{token}")
            logger.info("Удалено %d старых токенов для пользователя %s", len(tokens_to_remove), user_id)
