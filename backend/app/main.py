import logging
import os
from contextlib import asynccontextmanager
from dataclasses import asdict

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from api.auth.router import auth_router
from api.main_router import api_router
from config import GunicornConfig
from exceptions import setup_exception_handlers

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

logger = logging.getLogger(__name__)

def main():
    logger.info("Starting application")
    from gunicorn_application import Application
    gunicorn_config: GunicornConfig = GunicornConfig()
    gunicorn_app = Application(application=app, options=asdict(gunicorn_config))
    gunicorn_app.run()

if __name__ == "__main__":
    main()
