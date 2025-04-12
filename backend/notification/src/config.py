import os
from dataclasses import dataclass


@dataclass
class NatsConfig:
    nats_url = os.environ.get("NATS_URL", "nats://localhost:4222")


@dataclass
class SmtpConfig:
    smtp_password = os.environ.get("SMTP_PASSWORD")
    smtp_user = os.environ.get("SMTP_USER")
    smtp_host = os.environ.get("SMTP_HOST")
    smtp_port = os.environ.get("SMTP_PORT")


@dataclass
class ApiPathsConfig:
    email_code_url = os.environ.get("EMAIL_CODE_URL")


@dataclass
class Config:
    smtp = SmtpConfig()
    nats = NatsConfig()
    api_paths = ApiPathsConfig()
