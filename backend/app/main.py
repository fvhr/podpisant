import os
from dataclasses import asdict

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from api.router import api_router
from config import GunicornConfig

app = FastAPI(title="Podpdisant Api")

app.include_router(api_router)

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

def main():
    if not os.getenv("DEBUG"):
        from gunicorn_application import Application
        gunicorn_config: GunicornConfig = GunicornConfig()
        gunicorn_app = Application(application=app, options=asdict(gunicorn_config))
        gunicorn_app.run()


if __name__ == "__main__":
    main()
