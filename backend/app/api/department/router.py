from fastapi import APIRouter, Depends
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from api.department.schemas import DepartmentView
from database.models import Department, UserDepartment
from database.session_manager import get_db

department_router = APIRouter(prefix="/departments", tags=["departments"])


@department_router.get("/{org_id}")
async def get_departments_by_org_id(
    org_id: int,
    session: AsyncSession = Depends(get_db)
) -> list[DepartmentView]:
    departments = (await session.execute(
        select(Department)
        .where(Department.organization_id == org_id)
        .options(
            selectinload(Department.users).joinedload(UserDepartment.user)
        )
    )).scalars().all()

    return [await DepartmentView.from_db(department, session) for department in departments]
