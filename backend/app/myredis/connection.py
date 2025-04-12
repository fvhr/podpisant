from typing import AsyncIterator, Optional
from redis.asyncio import Redis, from_url

from config import REDIS_URI


async def get_redis() -> AsyncIterator[Redis]:
    async with Redis().from_url(REDIS_URI) as redis:
        yield redis
