from faststream.nats import NatsBroker

from src.config import NatsConfig
from src.presentation.email_router import email_router


def get_broker(config: NatsConfig):
    broker = NatsBroker(config.nats_url)
    broker.include_router(email_router)
    return broker
