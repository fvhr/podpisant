from email.message import EmailMessage

import aiosmtplib

from src.config import SmtpConfig


class SmtpService:
    def __init__(self, smtp_config: SmtpConfig):
        self.smtp_config = smtp_config

    async def send_email(self, to: str, subject: str, body: str):
        message = EmailMessage()
        message["Subject"] = subject
        message["From"] = self.smtp_config.smtp_user
        message["To"] = to
        message.set_content(body, subtype="html")

        await aiosmtplib.send(
            message,
            hostname=self.smtp_config.smtp_host,
            port=self.smtp_config.smtp_port,
            username=self.smtp_config.smtp_user,
            password=self.smtp_config.smtp_password,
            use_tls=True,
        )
