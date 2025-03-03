import glob
import logging
import os
import sys
import time
from logging.handlers import TimedRotatingFileHandler
from pythonjsonlogger import jsonlogger


class ArchivedTimedRotatingFileHandler(TimedRotatingFileHandler):
    """
    로그를 먼저 기본 경로에 기록한 후, rollover 시 ${LOG_PATH}/archived 폴더로 이동하여
    stempo-rhythm.%Y-%m-%d.%i.log 형식으로 파일명을 변경합니다.
    """

    def __init__(self, filename, archive_path, when='midnight', interval=1, backupCount=30, encoding=None, delay=False,
            utc=False):
        self.archive_path = archive_path
        os.makedirs(self.archive_path, exist_ok=True)
        super().__init__(filename, when, interval, backupCount, encoding, delay, utc)

    def doRollover(self):
        """
        기존 doRollover() 메서드를 확장하여, rollover 파일을 archive_path로 이동시키고, 파일명을
        stempo-rhythm.YYYY-MM-DD.i.log 형식으로 변경합니다.
        """
        if self.stream:
            self.stream.close()
            self.stream = None

        # 현재 로그 파일 이름
        current_log = self.baseFilename
        # rollover 시간
        current_time = int(time.time())
        # rollover 시 사용할 날짜 문자열 (UTC 여부에 따라 시간 조정)
        if self.utc:
            time_tuple = time.gmtime(self.rolloverAt - self.interval)
        else:
            time_tuple = time.localtime(self.rolloverAt - self.interval)
        date_str = time.strftime("%Y-%m-%d", time_tuple)

        # 기존 파일을 archive 폴더로 이동하면서, 파일명에 index를 붙임
        # 동일 날짜에 대해 여러 파일이 있다면, 인덱스를 1씩 증가
        pattern = os.path.join(self.archive_path, f"stempo-rhythm.{date_str}.*.log")
        existing_files = glob.glob(pattern)
        index = len(existing_files) + 1

        # 새 파일명 생성
        archive_filename = os.path.join(self.archive_path, f"stempo-rhythm.{date_str}.{index}.log")
        try:
            os.rename(current_log, archive_filename)
        except Exception:
            self.handleError(None)

        # 최대 로그 파일 수를 넘어가면, 오래된 파일부터 삭제
        if self.backupCount > 0:
            # 보관된 로그 파일에 대한 glob 패턴
            archived_logs = sorted(glob.glob(os.path.join(self.archive_path, "stempo-rhythm.*.*.log")))
            if len(archived_logs) > self.backupCount:
                for old_log in archived_logs[:len(archived_logs) - self.backupCount]:
                    os.remove(old_log)

        # 다음 rollover를 위해 rolloverAt 재설정
        if not self.delay:
            self.stream = self._open()

        # 새 rollover 시간 계산
        newRolloverAt = self.computeRollover(current_time)
        while newRolloverAt <= current_time:
            newRolloverAt = newRolloverAt + self.interval
        self.rolloverAt = newRolloverAt


def setup_logging(env: str = "default", log_path: str = None, max_file_size: str = "10MB", max_history: int = 30):
    """
    env: 'dev', 'test', 'prod', 또는 'default'
    log_path: 로그 파일 저장 경로 (prod 환경에서 사용됨)
    max_file_size: 사용하지 않음(시간 기반 롤링으로 대체)
    max_history: 보관할 최대 로그 파일 수
    """
    log_level = logging.INFO
    handler = None

    if env in ("dev", "test", "default"):
        if env == "test":
            log_level = logging.DEBUG
        else:
            log_level = logging.INFO
        handler = logging.StreamHandler(sys.stdout)
    elif env == "prod":
        log_level = logging.INFO
        # log_path가 주입되지 않으면 기본 경로 "logs" 사용
        if not log_path:
            log_path = "logs"
        os.makedirs(log_path, exist_ok=True)
        # archive 폴더 생성
        archive_path = os.path.join(log_path, "archived")
        os.makedirs(archive_path, exist_ok=True)
        # 로그 파일명 stempo-rhythm.log 사용 (로그는 기본 경로에 기록)
        log_file = os.path.join(log_path, "stempo-rhythm.log")
        # TimedRotatingFileHandler를 확장한 커스텀 핸들러 사용 (일별 rollover)
        handler = ArchivedTimedRotatingFileHandler(
                log_file,
                archive_path=archive_path,
                when="midnight",
                backupCount=max_history,
                utc=False
        )
    else:
        handler = logging.StreamHandler(sys.stdout)

    formatter = jsonlogger.JsonFormatter(
            '%(asctime)s %(levelname)s %(name)s %(message)s %(request_id)s %(transaction_id)s %(client_ip)s'
    )
    handler.setFormatter(formatter)

    root_logger = logging.getLogger()
    root_logger.handlers = []  # 기존 핸들러 제거
    root_logger.addHandler(handler)
    root_logger.setLevel(log_level)
