import glob
import logging
import os
import time
from logging.handlers import TimedRotatingFileHandler
from pythonjsonlogger import jsonlogger

from mdc_middleware import ContextFilter


class ArchivedTimedRotatingFileHandler(TimedRotatingFileHandler):
    """
    로그를 먼저 기본 경로에 기록한 후, rollover 시 ${LOG_PATH}/archived 폴더로 이동하여
    외부 주입받은 로그 파일명에 따라 stempo-rhythm.%Y-%m-%d.%i.log 형식으로 파일명을 변경합니다.
    """

    def __init__(self, filename, archive_path, when='midnight', interval=1, backupCount=30, encoding=None, delay=False,
            utc=False):
        self.archive_path = archive_path
        os.makedirs(self.archive_path, exist_ok=True)
        # 외부에서 주입받은 파일명에서 확장자를 제거하여 기본 이름으로 사용
        self.log_file_name = os.path.splitext(os.path.basename(filename))[0]
        super().__init__(filename, when, interval, backupCount, encoding, delay, utc)

    def doRollover(self):
        if self.stream:
            self.stream.close()
            self.stream = None

        current_log = self.baseFilename
        current_time = int(time.time())
        if self.utc:
            time_tuple = time.gmtime(self.rolloverAt - self.interval)
        else:
            time_tuple = time.localtime(self.rolloverAt - self.interval)
        date_str = time.strftime("%Y-%m-%d", time_tuple)

        # 외부에서 주입받은 log_file_name을 사용하여 패턴과 파일명을 생성
        pattern = os.path.join(self.archive_path, f"{self.log_file_name}.{date_str}.*.log")
        existing_files = glob.glob(pattern)
        index = len(existing_files) + 1

        archive_filename = os.path.join(self.archive_path, f"{self.log_file_name}.{date_str}.{index}.log")
        try:
            os.rename(current_log, archive_filename)
        except Exception:
            self.handleError(None)

        if self.backupCount > 0:
            archived_logs = sorted(glob.glob(os.path.join(self.archive_path, f"{self.log_file_name}.*.*.log")))
            if len(archived_logs) > self.backupCount:
                for old_log in archived_logs[:len(archived_logs) - self.backupCount]:
                    os.remove(old_log)

        if not self.delay:
            self.stream = self._open()

        newRolloverAt = self.computeRollover(current_time)
        while newRolloverAt <= current_time:
            newRolloverAt = newRolloverAt + self.interval
        self.rolloverAt = newRolloverAt


def setup_logging(env: str = "default", log_path: str = None, log_file: str = None, max_file_size: str = "10MB",
        max_history: int = 30):
    """
    env: 'dev', 'test', 'prod', 또는 'default'
    log_path: 로그 파일 저장 경로 (prod 환경에서 사용됨)
    log_file: 로그 파일명 (prod 환경에서 사용됨)
    max_file_size: 사용하지 않음 (시간 기반 롤링으로 대체)
    max_history: 보관할 최대 로그 파일 수
    """
    log_level = logging.INFO
    handler = None

    if env in ("dev", "test", "default"):
        log_level = logging.DEBUG if env == "test" else logging.INFO
        handler = logging.StreamHandler()
    elif env == "prod":
        log_level = logging.INFO
        if not log_path:
            log_path = "logs"
        if not log_file:
            log_file = "stempo-rhythm.log"
        os.makedirs(log_path, exist_ok=True)
        archive_path = os.path.join(log_path, "archived")
        os.makedirs(archive_path, exist_ok=True)
        log_file_path = os.path.join(log_path, log_file)
        handler = ArchivedTimedRotatingFileHandler(
                log_file_path,
                archive_path=archive_path,
                when="midnight",
                backupCount=max_history,
                utc=False
        )
    else:
        handler = logging.StreamHandler()

    formatter = jsonlogger.JsonFormatter(
            '%(asctime)s %(levelname)s %(name)s %(message)s %(request_id)s %(transaction_id)s %(client_ip)s'
    )
    handler.setFormatter(formatter)

    root_logger = logging.getLogger()
    root_logger.handlers = []  # 기존 핸들러 제거
    root_logger.addHandler(handler)

    # ContextFilter가 없으면 추가 (중복 방지)
    if not any(isinstance(f, ContextFilter) for f in root_logger.filters):
        root_logger.addFilter(ContextFilter())
    root_logger.setLevel(log_level)
