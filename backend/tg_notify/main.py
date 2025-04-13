import asyncio
import os

from nats.aio.client import Client as NATS
from aiogram import Bot
from typing import Dict
import logging

# Настройка логгирования
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class TelegramNotifier:
    def __init__(self, nats_url: str, bot_token: str):
        self.nats = NATS()
        self.bot = Bot(token=bot_token)
        self.nats_url = nats_url
        self.active_tasks: Dict[str, asyncio.Task] = {}

    async def connect(self):
        await self.nats.connect(self.nats_url)
        logger.info("Connected to NATS")

    async def handle_message(self, msg):
        try:
            data = msg.data.decode()
            user_id, message = data.split(":", 1)

            logger.info(f"Sending to user {user_id}: {message[:50]}...")
            await self.bot.send_message(chat_id=user_id, text=message)

        except Exception as e:
            logger.error(f"Error processing message: {e}")

    async def consume_forever(self):
        await self.nats.subscribe(
            "telegram.notifications",
            cb=self.handle_message,
            queue="telegram-workers"
        )
        logger.info("Subscribed to NATS topic")

        while True:
            try:
                await asyncio.sleep(1)  # Проверка соединения
                if not self.nats.is_connected:
                    logger.warning("Reconnecting to NATS...")
                    await self.connect()

            except asyncio.CancelledError:
                logger.info("Shutting down...")
                await self.nats.close()
                await self.bot.session.close()
                break
            except Exception as e:
                logger.error(f"Unexpected error: {e}")
                await asyncio.sleep(5)

TOKEN = os.getenv("BOT_TOKEN")

async def main():
    notifier = TelegramNotifier(
        nats_url="nats://localhost:4222",
        bot_token=TOKEN
    )

    await notifier.connect()
    await notifier.consume_forever()


if __name__ == "__main__":
    asyncio.run(main())