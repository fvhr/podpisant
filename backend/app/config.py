import os
from dataclasses import dataclass

DEBUG = os.getenv("DEBUG", "true").lower() not in ("false", "0")

if DEBUG:
    from dotenv import load_dotenv

    load_dotenv()

@dataclass
class GunicornConfig:
    bind: str = "0.0.0.0:8000"
    workers: int = 2
    timeout: int = 30
    worker_class: str = "uvicorn.workers.UvicornWorker"
