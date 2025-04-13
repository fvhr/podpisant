from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession

from api.organization.schemas import CreateOrganizationSchema
from database.models import Organization, organization
from database.session_manager import get_db

organization_router = APIRouter(prefix="/organizations", tags=["organizations"])

@organization_router.post("")
async def create_organization(organization_data: CreateOrganizationSchema, session: AsyncSession = Depends(get_db)):
    organization = Organization(**organization_data.model_dump())
    organization = await session.merge(organization)
    await session.commit()
    return {"oraganization_id": organization.id}


@organization_router.delete("/{organization_id}")
async def delete_organization(organization_id: int, session: AsyncSession = Depends(get_db)):
    await session.delete(organization)
    await session.commit()


# @organization_router.get("/{organization_id}/users")
# async def get_users_by_organization_id(
#     organization_id: int,
#     session: AsyncSession = Depends(get_db)
# ) -> list[OrganizationUserView]:
#     """
#     Получение всех пользователей организации с информацией о департаментах
#     """
#     try:
#         # Проверяем существование организации
#         org = await session.get(Organization, organization_id)
#         if not org:
#             raise HTTPException(status_code=404, detail="Organization not found")
#
#         # Получаем пользователей через связь UserOrganization и UserDepartment
#         users = (await session.execute(
#             select(User)
#             .join(UserOrganization, UserOrganization.user_id == User.id)
#             .outerjoin(UserDepartment, UserDepartment.user_id == User.id)
#             .where(UserOrganization.organization_id == organization_id)
#             .options(
#                 selectinload(User.departments).selectinload(UserDepartment.department)
#             )
#             .distinct()
#         )).scalars().all()
#
#         # Формируем ответ с информацией о департаментах
#         result = []
#         for user in users:
#             user_data = OrganizationUserView(
#                 **user.__dict__,
#                 department_ids=[ud.department_id for ud in user.departments],
#                 department_names=[ud.department.name for ud in user.departments if ud.department]
#             )
#             result.append(user_data)
#
#         return result
#
#     except Exception as e:
#         raise HTTPException(status_code=500, detail=str(e))
