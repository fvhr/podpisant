from pydantic import BaseModel

class AuthSchema(BaseModel):
    email: str

class AuthWithCodeSchema(BaseModel):
    code: str
    device_id: str


class AuthCodeSchema(BaseModel):
    code: str
