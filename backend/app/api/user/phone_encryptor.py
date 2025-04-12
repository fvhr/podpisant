import base64
import os

from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import padding
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes

from config import CRYPT_KEY


class PhoneEncryptor:
    def __init__(self, key: bytes):
        """
        :param key: 32-байтный ключ для AES-256 (например, os.urandom(32))
        """
        if len(key) != 32:
            raise ValueError("Ключ должен быть 32 байта для AES-256!")
        self.key = key

    def encrypt(self, phone: str) -> str:
        """Шифрует номер телефона и возвращает Base64 строку."""
        iv = os.urandom(16)  # Случайный вектор инициализации
        cipher = Cipher(
            algorithms.AES(self.key),
            modes.GCM(iv),
            backend=default_backend()
        )
        encryptor = cipher.encryptor()
        padded_phone = self._pad(phone.encode())
        ciphertext = encryptor.update(padded_phone) + encryptor.finalize()
        encrypted_data = iv + encryptor.tag + ciphertext
        return base64.b64encode(encrypted_data).decode()

    def decrypt(self, encrypted_phone: str) -> str:
        """Дешифрует Base64 строку обратно в номер телефона."""
        encrypted_data = base64.b64decode(encrypted_phone.encode())
        iv = encrypted_data[:16]
        tag = encrypted_data[16:32]
        ciphertext = encrypted_data[32:]
        cipher = Cipher(
            algorithms.AES(self.key),
            modes.GCM(iv, tag),
            backend=default_backend()
        )
        decryptor = cipher.decryptor()
        padded_phone = decryptor.update(ciphertext) + decryptor.finalize()
        return self._unpad(padded_phone).decode()

    @staticmethod
    def _pad(data: bytes) -> bytes:
        """Добавляет padding для AES (PKCS7)."""
        padder = padding.PKCS7(128).padder()
        return padder.update(data) + padder.finalize()

    @staticmethod
    def _unpad(data: bytes) -> bytes:
        """Убирает padding после дешифровки."""
        unpadder = padding.PKCS7(128).unpadder()
        return unpadder.update(data) + unpadder.finalize()


encryptor = PhoneEncryptor(CRYPT_KEY)

# phone = "+79161234567"
# encrypted_phone = encryptor.encrypt(phone)
# decrypted_phone = encryptor.decrypt(encrypted_phone_from_db)
# print(f"Расшифрованный телефон: {decrypted_phone}")
