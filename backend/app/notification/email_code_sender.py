import logging
from uuid import UUID

import redis.asyncio as aioredis
from fastapi import Depends
from nats.aio.client import Client

from app_errors.user_errors import UserNotFoundErrorByCode
from notification.nats_utils import get_nats_client, NatsConfig, send_via_nats
from myredis.connection import get_redis

logger = logging.getLogger(__name__)


class EmailCodeSender:
    def __init__(
        self,
        redis: aioredis.Redis,
        nats_client: Client,
        nats_config: NatsConfig,
    ):
        self._nats_client = nats_client
        self._nats_config = nats_config
        self._redis = redis

    async def email_login_notify(self, user_email: str, user_id: UUID, email_code: str, device_id: str) -> None:
        data = {
            "email": user_email, "email_code": email_code,
        }
        logger.info(
            "email_register_notify: data: %s  sub: %s",
            data,
            self._nats_config.email_confirmation_sub,
        )
        try:
            await send_via_nats(
                self._nats_client,
                self._nats_config.email_confirmation_sub,
                data=data,
            )
            await self.save_code(email_code, user_id, device_id)
        except Exception as e:
            await self.delete_code(email_code, device_id)

    async def save_code(
        self, email_code: str, user_id: UUID, device_id: str
    ) -> None:
        await self._redis.set(
            f"login_code:{email_code}:{device_id}",
            str(user_id),
            ex=1800,
        )

    async def get_user_id_by_code(
        self, email_code: str, device_id: str
    ) -> UUID | None:
        user_id_bytes = await self._redis.get(
            f"login_code:{email_code}:{device_id}"
        )

        if user_id_bytes is None:
            raise UserNotFoundErrorByCode(email_code)

        user_id_str = user_id_bytes.decode('utf-8')

        return UUID(user_id_str)

    async def delete_code(
        self, email_code: str, device_id: str
    ) -> None:
        await self._redis.delete(
            f"login_code:{email_code}:{device_id}"
        )

def get_email_code_sender(
    redis: aioredis.Redis = Depends(get_redis),
    nats_client: Client = Depends(get_nats_client),
    nats_config: NatsConfig = Depends(NatsConfig.from_env)
) -> EmailCodeSender:
    yield EmailCodeSender(redis=redis, nats_client=nats_client, nats_config=nats_config)
