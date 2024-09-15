import os

import sys
from pydub import AudioSegment
from pydub.generators import Sine


# 메트로놈 비트 생성
def create_metronome_bpm(bpm, bit, tone_duration=100):
    frequency = 440.0  # 메트로놈 주파수
    sine_wave_strong = Sine(frequency)  # 강한 첫 박자
    sine_wave_weak = Sine(frequency * 0.6)  # 약한 박자

    interval_ms = (60 / bpm) * 1000  # 비트 간의 간격 계산

    # 첫 박자는 강하게, 나머지는 약하게
    strong_tone = sine_wave_strong.to_audio_segment(duration=tone_duration)  # 첫 박자
    weak_tone = sine_wave_weak.to_audio_segment(duration=tone_duration)  # 나머지 박자

    silence = AudioSegment.silent(duration=interval_ms - tone_duration)  # 비트 간의 정적

    # 비트 패턴 생성
    rhythm = strong_tone + silence
    for _ in range(1, bit):  # 첫 박자 이후 약한 박자들 추가
        rhythm += weak_tone + silence

    return rhythm

# 리듬 생성 및 파일 저장
def main():
    if len(sys.argv) < 3:
        print("Usage: python create_rhythm.py <bpm> <bit>")
        return

    bpm = int(sys.argv[1])  # BPM 입력
    bit = int(sys.argv[2])  # 비트 수 입력

    output_dir = os.path.join(os.getcwd(), "generated")  # 출력 디렉토리 설정
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)  # 디렉토리 생성

    output_filename = f"rhythm_{bpm}_{bit}_bpm.wav"  # 파일 이름
    output_path = os.path.join(output_dir, output_filename)  # 파일 경로

    combined_rhythm = create_metronome_bpm(bpm, bit)  # 비트에 맞는 리듬 생성
    combined_rhythm.export(output_path, format="wav")  # 파일로 저장

    print(f"Rhythm created: {output_path}")

if __name__ == "__main__":
    main()
