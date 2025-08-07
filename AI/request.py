import requests
import base64
from PIL import Image
import io

# 예시: 서버에 이미지 업로드 후 받은 response
url = 'http://localhost:8080/predict'
with open('testimg/sample_image.png', 'rb') as f:
    response = requests.post(url, files={'image': f})
res_json = response.json()
print(res_json["detections"])
# crops를 순회하며 이미지로 저장/띄우기
for idx, b64_img in enumerate(res_json['crops']):
    img_bytes = base64.b64decode(b64_img)
    img = Image.open(io.BytesIO(img_bytes))
    img.save(f'crop_{idx}.jpeg')    # 파일로 저장
    img.show()                     # 바로 보기 (윈도우라면 이미지 뷰어로 뜸)