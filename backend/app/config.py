import logging
import os
import tomllib
from dataclasses import dataclass
from pathlib import Path

DEBUG = os.getenv("DEBUG", "true").lower() not in ("false", "0")

if DEBUG:
    from dotenv import load_dotenv

    load_dotenv()

TOML_CONFIG_PATH = (
    Path(__file__).resolve().parent.parent.parent.parent / "config" / "config.toml"
)
LOGGING_CONFIG_PATH = (
    Path(__file__).resolve().parent.parent.parent.parent / "config" / "logging.yaml"
)


@dataclass
class GunicornConfig:
    bind: str = "0.0.0.0:8000"
    workers: int = 2
    timeout: int = 30
    worker_class: str = "uvicorn.workers.UvicornWorker"


logger = logging.getLogger(__name__)
