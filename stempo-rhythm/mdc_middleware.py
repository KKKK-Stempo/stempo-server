import uuid
import logging
from fastapi import Request
from starlette.middleware.base import BaseHTTPMiddleware
from contextvars import ContextVar

# 컨텍스트 변수 정의 (MDC와 유사한 역할)
request_id_ctx: ContextVar[str] = ContextVar("request_id", default="anonymous")
transaction_id_ctx: ContextVar[str] = ContextVar("transaction_id", default="")
client_ip_ctx: ContextVar[str] = ContextVar("client_ip", default="")

# 로그 레코드에 MDC 정보를 추가하는 필터
class ContextFilter(logging.Filter):
    def filter(self, record):
        record.request_id = request_id_ctx.get()
        record.transaction_id = transaction_id_ctx.get()
        record.client_ip = client_ip_ctx.get()
        return True

# 미들웨어 정의
class MDCMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
        req_id = request.headers.get("X-Request-Id", str(uuid.uuid4()))
        trans_id = request.headers.get("X-Transaction-Id", str(uuid.uuid4()))
        client_ip = request.client.host if request.client else "unknown"

        request_id_ctx.set(req_id)
        transaction_id_ctx.set(trans_id)
        client_ip_ctx.set(client_ip)

        logger = logging.getLogger()
        logger.addFilter(ContextFilter())

        response = await call_next(request)
        return response
