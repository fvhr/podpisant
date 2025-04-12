from dataclasses import dataclass
from typing import ClassVar

@dataclass(eq=False)
class AppError(Exception):
    """Base Error."""

    status: ClassVar[int] = 500

    @property
    def title(self) -> str:
        return "An app error occurred"


class DomainError(AppError): ...


class ApplicationError(AppError):
    """Base Application Exception."""

    @property
    def title(self) -> str:
        return "An application error occurred"


class UnexpectedError(ApplicationError):
    pass


class RepoError(UnexpectedError):
    pass



@dataclass(eq=False)
class InvalidTokenError(ApplicationError):
    def title(self) -> str:
        return "Invalid token."


@dataclass(eq=False)
class TokenExpiredError(ApplicationError):
    def title(self) -> str:
        return "Token expired."

@dataclass(eq=False)
class DeviceMismatchException(ApplicationError):
    def title(self) -> str:
        return "Device mismatch."
