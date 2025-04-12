from dataclasses import dataclass

from src.application.email_notification.smtp_service import SmtpService
from src.config import ApiPathsConfig


@dataclass
class EmailCodeCommand:
    email: str
    email_code: str


class EmailCodeHandler:
    def __init__(self, smtp_service: SmtpService, api_paths_conf: ApiPathsConfig):
        self.smtp_service = smtp_service
        self.api_paths_conf = api_paths_conf

    async def handle(self, command: EmailCodeCommand):
        body = f"""
        Здравствуйте! Ваш код для входа в систему Подписант:
        {command.email_code}
        Если вы не запрашивали данный код, то проигнорируйте это письмо."""
        await self.smtp_service.send_email(command.email, "Вход в систему Подписант", body)
