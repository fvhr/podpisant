import logging
import os

from dishka.integrations.base import FromDishka
from faststream.nats import NatsRouter

from src.application.email_notification.email_code.handler import EmailCodeHandler, \
    EmailCodeCommand

email_router = NatsRouter()

# class EmailConfirmationSchema(BaseModel):
#     email: EmailStr
#     email_confirmation_token: str

# @nats_router.subscriber("email.reset_password")


logger = logging.getLogger(__name__)


@email_router.subscriber(os.getenv("EMAIL_CODE_SUB", "email.code"))
async def handle_email_code(command: EmailCodeCommand, handler: FromDishka[EmailCodeHandler]) -> None:
    logger.info(f"Received command: {command}")
    await handler.handle(command)
