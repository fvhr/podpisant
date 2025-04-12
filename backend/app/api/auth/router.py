from uuid import uuid4

from fastapi import APIRouter, Depends
import secrets

from fastapi.responses import ORJSONResponse
from sqlalchemy.ext.asyncio import AsyncSession

from api.auth.tokens.auth_service import HttpAuthServerService, get_auth_service, DeviceId
from api.auth.tokens.tokens import set_tokens
from api.auth.user_service import get_user_by_email, get_user_by_id
from notification.email_code_sender import EmailCodeSender, get_email_code_sender
from api.auth.schema import AuthSchema, AuthWithCodeSchema, AuthCodeSchema
from database.session_manager import get_db

auth_router = APIRouter(prefix="", tags=["Auth"])


@auth_router.post("/login")
async def get_connected_clients(
    data: AuthSchema,
    session: AsyncSession = Depends(get_db),
    email_code_sender: EmailCodeSender = Depends(get_email_code_sender)
) -> ORJSONResponse:
    user = await get_user_by_email(session, data.email)
    device_id = str(uuid4())
    code = f"{secrets.randbelow(10000):04d}"
    await email_code_sender.email_login_notify(user.email, user.id, code, device_id)

    return ORJSONResponse({"status": "success", "device_id": device_id})


@auth_router.post("/login-with-code")
async def get_connected_clients(
    data: AuthWithCodeSchema,
    session: AsyncSession = Depends(get_db),
    email_code_sender: EmailCodeSender = Depends(get_email_code_sender),
    auth_service: HttpAuthServerService = Depends(get_auth_service)
) -> ORJSONResponse:
    user_id = await email_code_sender.get_user_id_by_code(data.code, data.device_id)
    user = await get_user_by_id(session, user_id)
    access_token, refresh_token = await auth_service.create_and_save_tokens(user)
    await email_code_sender.delete_code(data.code, data.device_id)
    response = ORJSONResponse({"status": "success"})
    set_tokens(response, access_token, refresh_token)
    return response
