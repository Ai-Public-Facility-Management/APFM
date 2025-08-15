import requests
import json
import os

# 서버 주소 (FastAPI 서버가 실행 중이어야 함)
API_URL = "http://localhost:8080/predict-high"

# 테스트할 이미지 폴더 경로 (실제 경로로 변경하세요)
image_folder_path = "testimg"  # 예: "D:/cctv_images"

# 폴더 존재 여부 확인
if not os.path.isdir(image_folder_path):
    print(f"❌ 유효하지 않은 경로입니다: {image_folder_path}")
    exit()

# 요청 payload 구성
payload = {
    "image_folder": image_folder_path
}

# POST 요청
print(f"🚀 요청 중: {API_URL}")
response = requests.post(API_URL, json=payload)

# 응답 확인
if response.status_code == 200:
    result = response.json()
    print("✅ 응답 결과:")
    print(json.dumps(result, indent=2, ensure_ascii=False))  # 한글도 잘 보이게
else:
    print(f"❌ 오류 발생: {response.status_code}")
    print(response.text)
