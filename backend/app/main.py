import logging
import os
from contextlib import asynccontextmanager
from dataclasses import asdict

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from api.auth.router import auth_router
from api.main_router import api_router
from config import GunicornConfig, get_logging_config, logging_path
from exceptions import setup_exception_handlers
from log.main import configure_logging

app = FastAPI(title="Podpdisant Api", root_path="/api")

app.include_router(api_router)
app.include_router(auth_router)

origins = ["*"]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["GET", "POST", "OPTIONS", "DELETE", "PATCH", "PUT"],
    allow_headers=[
        "Content-Type",
        "Set-Cookie",
        "Access-Control-Allow-Headers",
        "Access-Control-Allow-Origin",
        "Authorization",
    ],
)

setup_exception_handlers(app)
logging_config = get_logging_config(logging_path)
configure_logging(logging_config)

logger = logging.getLogger(__name__)

def main():
    logger.info("Starting application")
    from gunicorn_application import Application
    gunicorn_config: GunicornConfig = GunicornConfig()
    gunicorn_app = Application(application=app, options=asdict(gunicorn_config))
    gunicorn_app.run()

if __name__ == "__main__":
    main()
