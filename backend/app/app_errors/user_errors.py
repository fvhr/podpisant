from pydantic.dataclasses import dataclass

from app_errors.application_errors import DomainError, ApplicationError


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


@dataclass(eq=False)
class UserNotFoundErrorByCode(ApplicationError):
    code: str

    @property
    def title(self) -> str:
        return f"User not found by code {self.code}"


@dataclass(eq=False)
class UserNotSuperAdmin(ApplicationError):

    @property
    def title(self) -> str:
        return f"User not super admin"
