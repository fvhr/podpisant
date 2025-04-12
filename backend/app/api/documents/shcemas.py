from pydantic import BaseModel


class CreateDocumentSchema(BaseModel):
    name: str
    stages: list[StageSchema]


class StageSchema(BaseModel):

