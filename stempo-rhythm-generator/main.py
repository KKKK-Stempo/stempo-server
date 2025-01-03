import math
from fastapi import FastAPI, Body, Response, HTTPException
from io import BytesIO
from pydantic import BaseModel, Field
from pydub import AudioSegment
from pydub.generators import Sine

app = FastAPI()


def create_metronome_bpm(
        *,
        bpm: int,
        bit: int,
        tone_duration: int = 100,
        volume_factor: float = 1.5
) -> AudioSegment:
    """
    BPM과 비트 수를 기반으로 메트로놈 리듬을 생성합니다.

    Args:
        bpm (int): 분당 비트 수 (Beats Per Minute).
        bit (int): 비트의 수.
        tone_duration (int, optional): 각 톤의 지속 시간 (밀리초 단위). 기본값은 100ms입니다.
        volume_factor (float, optional): 볼륨 조절을 위한 배수. 기본값은 1.5입니다.

    Returns:
        AudioSegment: 생성된 리듬의 오디오 세그먼트.
    """
    # 유효성 검증
    if bpm <= 0 or bit <= 0:
        raise ValueError("BPM과 비트는 양의 정수여야 합니다.")

    interval_ms = (60 / bpm) * 1000  # 비트 간 간격 (밀리초 단위)
    if tone_duration >= interval_ms:
        raise ValueError("tone_duration은 bpm에서 계산된 비트 간격보다 작아야 합니다.")

    frequency = 440.0  # 메트로놈 주파수 (Hz)
    sine_wave_strong = Sine(frequency)  # 강한 첫 박자
    sine_wave_weak = Sine(frequency * 0.6)  # 약한 박자

    strong_tone = sine_wave_strong.to_audio_segment(duration=tone_duration)
    weak_tone = sine_wave_weak.to_audio_segment(duration=tone_duration)

    # 볼륨 조절 (데시벨 단위)
    volume_increase_db = 20 * math.log10(volume_factor)
    strong_tone += volume_increase_db
    weak_tone += volume_increase_db

    silence = AudioSegment.silent(duration=interval_ms - tone_duration)

    # 비트 패턴 생성 (첫 박자 강음 + 나머지 약음)
    rhythm = strong_tone + silence
    for _ in range(1, bit):
        rhythm += weak_tone + silence

    return rhythm


# 요청 바디 모델 정의
class RhythmRequest(BaseModel):
    bpm: int = Field(..., ge=10, le=200, description="BPM은 10에서 200 사이의 정수여야 합니다.")
    bit: int = Field(..., ge=1, le=8, description="Bit는 1에서 8 사이의 정수여야 합니다.")


@app.post("/api/v1/rhythm", response_class=Response, responses={
    200: {"description": "리듬 WAV 파일이 성공적으로 생성되었습니다."},
    400: {"description": "잘못된 입력 파라미터."},
    500: {"description": "내부 서버 오류."}
})
def create_rhythm(request: RhythmRequest = Body(...)) -> Response:
    """
    BPM과 비트 수를 받아 메트로놈 리듬 WAV 파일을 생성하는 엔드포인트입니다.

    Args:
        request (RhythmRequest): 리듬 생성 요청 바디. BPM과 비트 정보를 포함.

    Returns:
        Response: 생성된 WAV 오디오 파일의 바이너리 데이터.
    """
    try:
        rhythm = create_metronome_bpm(bpm=request.bpm, bit=request.bit)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=f"잘못된 입력입니다: {str(e)}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"리듬 생성에 실패했습니다: {str(e)}")

    output_buffer = BytesIO()
    rhythm.export(output_buffer, format="wav")
    output_buffer.seek(0)

    return Response(content=output_buffer.read(), media_type="audio/wav")


@app.get("/health", response_model=dict)
def health() -> dict:
    """
    서비스의 상태를 확인하는 헬스 체크 엔드포인트입니다.

    Returns:
        dict: 서비스 상태.
    """
    return {"status": "UP"}
