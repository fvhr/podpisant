from pydantic import BaseModel


class EmailCodeSchema(BaseModel):
    email: str
    email_code_token: str
