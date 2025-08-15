# request.py
import requests
import os

API_URL = "http://localhost:8080/predict-board"

# 테스트할 이미지 경로
image_path = "testimg/sample.jpg"

if not os.path.exists(image_path):
    print(f"❌ 파일이 존재하지 않습니다: {image_path}")
    exit()

with open(image_path, "rb") as f:
    files = {"image": f}
    print(f"🚀 요청 중: {API_URL}")
    response = requests.post(API_URL, files=files)

if response.status_code == 200:
    print("✅ 응답 결과:")
    print(response.json())
else:
    print(f"❌ 오류 발생: {response.status_code}")
    print(response.text)
