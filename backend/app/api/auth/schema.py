from pydantic import BaseModel

class AuthSchema(BaseModel):
    email: str

class AuthWithCodeSchema(AuthSchema):
    code: str

class AuthCodeSchema(BaseModel):
    code: str
