import logging
import os
from dataclasses import dataclass
from pathlib import Path

import yaml
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

base_path = Path(__file__).parent
logging_path = base_path / "log" / "logging.yaml"

def get_logging_config(logging_path):
    with open(logging_path) as f:
        logging_config = yaml.safe_load(f)
    return logging_config

CRYPT_KEY = os.environ.get("CRYPT_KEY")
