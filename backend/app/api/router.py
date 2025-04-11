from fastapi import APIRouter

api_router = APIRouter(prefix="/api/v1", tags=["Api"])


@api_router.get("/")
async def get_connected_clients():
    return {"status": "ok"}
