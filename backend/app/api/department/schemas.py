from typing import List
from uuid import UUID
from pydantic import BaseModel
from datetime import datetime
from sqlalchemy.ext.asyncio import AsyncSession

from sqlalchemy.orm import selectinload, joinedload

from api.user.phone_encryptor import encryptor
from database.models import Department


class DepartmentUserView(BaseModel):
    id: UUID
    fio: str


class DepartmentView(BaseModel):
    id: int
    name: str
    description: str
    created_at: datetime | None
    users: List[DepartmentUserView]

    @classmethod
    async def from_db(cls, department: Department, session: AsyncSession) -> "DepartmentView":
        await session.refresh(department, ['users'])

        user_departments = department.users
        users = [ud.user for ud in user_departments if ud.user is not None]

        return cls(
            id=department.id,
            name=department.name,
            description=department.desc,
            created_at=department.created_at,
            users=[
                DepartmentUserView(
                    id=user.id,
                    fio=encryptor.decrypt(user.fio)
                ) for user in users
            ]
        )