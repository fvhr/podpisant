import logging

from dishka import make_async_container
from dishka.integrations import faststream as faststream_integration
from faststream import FastStream

from src.config import Config
from src.infra.broker import get_broker
from src.ioc import AppProvider, ConfigProvider

config = Config()

container = make_async_container(ConfigProvider(), AppProvider(), context={Config: config})
broker = get_broker(config.nats)

logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")


def get_faststream_app() -> FastStream:
    faststream_app = FastStream(broker)
    faststream_integration.setup_dishka(container, faststream_app, auto_inject=True)
    return faststream_app

