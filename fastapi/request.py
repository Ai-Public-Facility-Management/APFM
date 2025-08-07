import requests

# FastAPI 서버 주소
url = "http://localhost:8000/estimate"

# 보낼 이미지 파일 경로
image_path = "sample_image.png"  # 실제 이미지 경로로 변경하세요

# 파일 열어서 POST 요청
with open(image_path, "rb") as image_file:
    files = {"image": image_file}
    response = requests.post(url, files=files)

# 결과 출력
if response.status_code == 200:
    data = response.json()
    print("📷 시설 분석 결과:", data["vision_analysis"])
    print("💰 견적 결과:", data["cost_estimate"])
else:
    print("❌ 요청 실패:", response.status_code)
    print(response.text)