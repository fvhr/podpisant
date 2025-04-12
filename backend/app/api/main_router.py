from fastapi import APIRouter

api_router = APIRouter(tags=["Api"])

@api_router.get("/")
async def get_connected_clients():
    return {"status": "ok"}
