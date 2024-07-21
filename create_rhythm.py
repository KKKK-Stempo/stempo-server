import os
import sys

from pydub import AudioSegment
from pydub.generators import Sine


# 메트로놈 비트 생성
def create_metronome_bpm(bpm, tone_duration=100):
    frequency = 440.0  # 메트로놈 주파수
    sine_wave = Sine(frequency)

    interval_ms = (60 / bpm) * 1000  # 비트 간의 간격 계산

    tone = sine_wave.to_audio_segment(duration=tone_duration)  # 비트 길이
    silence = AudioSegment.silent(duration=interval_ms - tone_duration)  # 비트 간의 정적

    return tone + silence  # 비트와 정적 결합

# 리드미컬한 큐잉 생성
def create_keyboard_rhythm(bpm, tone_duration=100):
    frequency = 440.0  # 키보드 주파수
    sine_wave = Sine(frequency)

    interval_ms = (60 / bpm) * 1000  # 비트 간격
    tone = sine_wave.to_audio_segment(duration=tone_duration)  # 키보드 톤
    silence = AudioSegment.silent(duration=interval_ms - tone_duration)  # 정적 구간

    rhythm = AudioSegment.empty()
    for _ in range(10):  # 10번 반복
        rhythm += tone + silence  # 리듬 패턴 생성

    return rhythm

# 메트로놈 비트와 리드미컬한 큐잉 결합
def create_combined_rhythm(bpm):
    metronome = create_metronome_bpm(bpm)  # 메트로놈 비트 생성
    keyboard_rhythm = create_keyboard_rhythm(bpm)  # 키보드 리듬 생성

    combined_rhythm = AudioSegment.empty()
    for _ in range(10):  # 10번 반복
        combined_rhythm += metronome  # 메트로놈 추가
        combined_rhythm += keyboard_rhythm  # 키보드 리듬 추가

    return combined_rhythm  # 결합된 리듬 반환

# 리듬 생성 및 파일 저장
def main():
    if len(sys.argv) < 2:
        print("Usage: python create_rhythm.py <bpm>")
        return

    bpm = int(sys.argv[1])  # BPM 입력

    output_dir = os.path.join(os.getcwd(), "generated")  # 출력 디렉토리 설정
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)  # 디렉토리 생성

    output_filename = f"rhythm_{bpm}_bpm.wav"  # 파일 이름
    output_path = os.path.join(output_dir, output_filename)  # 파일 경로

    combined_rhythm = create_combined_rhythm(bpm)  # 메트로놈과 키보드 결합
    combined_rhythm.export(output_path, format="wav")  # 파일로 저장

    print(f"Rhythm created: {output_path}")

if __name__ == "__main__":
    main()
