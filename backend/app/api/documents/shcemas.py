from pydantic import BaseModel


class CreateDocumentSchema(BaseModel):
    name: str
    organization_id: int


#     stages: list[StageSchema]
#
# class StageSchema(BaseModel):

