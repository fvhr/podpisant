from dataclasses import dataclass
from datetime import datetime
from typing import NewType, TypedDict
from uuid import UUID


AccessToken = NewType("AccessToken", str)
RefreshToken = NewType("RefreshToken", str)

class FullAccessTokenPayload(TypedDict, total=False):
    """Типизированный словарь для представления данных в payload JWT."""
    sub: UUID
    exp: datetime
    iat: datetime
    jti: UUID


class AccessTokenPayload(TypedDict):
    sub: str


class JwtToken(TypedDict):
    token: str
    created_at: datetime
    expires_at: datetime


class RefreshTokenPayload(TypedDict):
    sub: str
    jti: UUID


@dataclass
class RefreshTokenData:
    user_id: UUID
    jti: str
    created_at: datetime

@dataclass
class RefreshTokenWithData(RefreshTokenData):
    token: RefreshToken
