import logging
import uuid
from contextvars import ContextVar
from fastapi import Request
from starlette.middleware.base import BaseHTTPMiddleware

# 컨텍스트 변수 정의
request_id_ctx: ContextVar[str] = ContextVar("request_id", default="unknown")
transaction_id_ctx: ContextVar[str] = ContextVar("transaction_id", default="unknown")
client_ip_ctx: ContextVar[str] = ContextVar("client_ip", default="unknown")


# 로그 레코드에 MDC 정보를 추가하는 필터
class ContextFilter(logging.Filter):
    def filter(self, record):
        # 요청별 ContextVar 값을 실시간으로 반영
        record.request_id = request_id_ctx.get()
        record.transaction_id = transaction_id_ctx.get()
        record.client_ip = client_ip_ctx.get()
        return True


# 필터를 전역적으로 한 번만 추가
logger = logging.getLogger()
if not any(isinstance(f, ContextFilter) for f in logger.filters):
    logger.addFilter(ContextFilter())


# 미들웨어 정의
class MDCMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
        # 클라이언트에서 보낸 헤더를 가져옴
        req_id = request.headers.get("X-Request-Id", str(uuid.uuid4()))
        trans_id = request.headers.get("X-Transaction-Id", str(uuid.uuid4()))
        client_ip = request.headers.get("X-Client-Ip") or request.client.host or "unknown"

        # 컨텍스트 변수 설정
        token_request_id = request_id_ctx.set(req_id)
        token_transaction_id = transaction_id_ctx.set(trans_id)
        token_client_ip = client_ip_ctx.set(client_ip)

        try:
            response = await call_next(request)
        finally:
            # 컨텍스트 변수 초기화
            request_id_ctx.reset(token_request_id)
            transaction_id_ctx.reset(token_transaction_id)
            client_ip_ctx.reset(token_client_ip)

        return response
