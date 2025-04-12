import json
import logging
import os
from typing import AsyncIterator

from fastapi import Depends
from nats.aio.client import Client


logger = logging.getLogger(__name__)


class NatsConfig:
    def __init__(
        self, uri: str, email_code_sub: str
    ):
        self.uri = uri
        self.email_confirmation_sub = email_code_sub

    @staticmethod
    def from_env() -> "NatsConfig":
        return NatsConfig(
            uri=os.getenv("NATS_URI"),
            email_code_sub=os.getenv("EMAIL_CODE_SUB"),
        )


async def send_via_nats(
    nats_client: Client,
    subject: str,
    json_message: str | None = None,
    data: dict | None = None,
    string: str | None = None,
):
    if json_message:
        await nats_client.publish(subject, json_message.encode("utf-8"))
    elif data:
        await nats_client.publish(subject, json.dumps(data).encode("utf-8"))
    elif string:
        await nats_client.publish(subject, string.encode("utf-8"))


async def get_nats_client(nats_config: NatsConfig = Depends(NatsConfig.from_env)) -> AsyncIterator[Client]:
    client = Client()
    await client.connect(servers=["nats://nats:4222"])
    try:
        yield client
    finally:
        await client.close()
