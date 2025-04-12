from pydantic.dataclasses import dataclass

from app_errors.application_errors import DomainError


@dataclass(eq=False)
class UserNotFoundByIdError(DomainError):
    user_id: str

    @property
    def title(self) -> str:
        return f"User with id {self.user_id} not found."


@dataclass(eq=False)
class UserAlreadyExistsError(DomainError):
    email: str

    @property
    def title(self) -> str:
        return f"{self.email} is already registered."


@dataclass(eq=False)
class UserNotFoundByEmailError(DomainError):
    email: str

    @property
    def title(self) -> str:
        return f"{self.email} doesn't exists."


@dataclass(eq=False)
class UnauthenticatedUserError(DomainError):
    @property
    def title(self) -> str:
        return "Unauthenticated user."
