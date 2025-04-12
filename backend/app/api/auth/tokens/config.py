from pathlib import Path

from pydantic import BaseModel


class JWTSettings(BaseModel):
    private_key_path: Path = (
        Path(__file__).parent.parent.parent.parent.parent
        / "certs"
        / "jwt-private.pem"
    )
    public_key_path: Path = (
        Path(__file__).parent.parent.parent.parent.parent
        / "certs"
        / "jwt-public.pem"
    )
    algorithm: str = "RS512"
    access_token_expire_minutes: int = 1500
    refresh_token_expire_days: int = 30
    refresh_token_by_user_limit: int = 5

    _private_key: str = None
    _public_key: str = None

    def __post_init__(self):
        if self._private_key is None:
            self._private_key = self.private_key_path.read_text()
        if self._public_key is None:
            self._public_key = self.public_key_path.read_text()

    @property
    def private_key(self) -> str:
        if self._private_key is None:
            self._private_key = self.private_key_path.read_text()
        return self._private_key

    @property
    def public_key(self) -> str:
        if self._public_key is None:
            self._public_key = self.public_key_path.read_text()
        return self._public_key
