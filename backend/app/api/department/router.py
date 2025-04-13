from datetime import datetime
from zoneinfo import ZoneInfo

from fastapi import APIRouter, Depends
from fastapi.responses import ORJSONResponse
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from api.auth.idp import get_current_user
from api.department.schemas import DepartmentView, CreateDepartmentSchema
from database.models import Department, UserDepartment, User
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


@department_router.post("/{org_id}")
async def create_department(
    org_id: int,
    department_data: CreateDepartmentSchema,
    session: AsyncSession = Depends(get_db)
) -> ORJSONResponse:
    department = Department(
        name=department_data.name,
        desc=department_data.description,
        organization_id=org_id,
        created_at=datetime.now(ZoneInfo("Europe/Moscow"))
    )

    session.add(department)

    await session.flush()

    await session.commit()
    return ORJSONResponse({
        "department_id": department.id,
    })

