import logging
import os
from dataclasses import dataclass

from dotenv import load_dotenv


@dataclass
class GunicornConfig:
    bind: str = "0.0.0.0:8000"
    workers: int = 2
    timeout: int = 30
    worker_class: str = "uvicorn.workers.UvicornWorker"

load_dotenv()

DB_HOST = os.environ.get("DB_HOST")
DB_PORT = os.environ.get("DB_PORT")
DB_NAME = os.environ.get("DB_NAME")
DB_USER = os.environ.get("DB_USER")
DB_PASS = os.environ.get("DB_PASS")
DATABASE_URI = os.environ.get(
    "DATABASE_URI",
    f"postgresql+asyncpg://"
    f"{DB_USER}:{DB_PASS}@{DB_HOST}:{DB_PORT}/{DB_NAME}",
)

logger = logging.getLogger(__name__)

REDIS_HOST = os.environ.get("REDIS_HOST")
REDIS_PORT = os.environ.get("REDIS_PORT")
REDIS_URI = f"redis://{REDIS_HOST}:{REDIS_PORT}/0"

NATS_URI = os.environ.get("NATS_URI")
