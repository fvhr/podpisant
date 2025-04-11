import os
from dataclasses import dataclass

DEBUG = not os.getenv("DEBUG", "false").lower() in ("false", "0")

@dataclass
class GunicornConfig:
    bind: str = "0.0.0.0:8000"
    workers: int = 2
    timeout: int = 30
    worker_class: str = "uvicorn.workers.UvicornWorker"
