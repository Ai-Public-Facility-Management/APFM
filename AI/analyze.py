import base64
from openai import OpenAI
import os



def analyze_facility_image(image_path: str) -> str:
    # 환경변수에서 API 키 불러오기
    client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))    
    with open(image_path, "rb") as f:
        base64_image = base64.b64encode(f.read()).decode("utf-8")
    
    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[
            {
                "role": "system",
                "content": "너는 CCTV 이미지 속 시설물 파손과 보행 공간의 상태를 분석해 견적 산출을 위한 정량적인 설명을 생성하는 전문 AI야. 면적은 제곱미터 단위로 수치화해서 제공해야 해. 면적과 탐지된 시설물, 시설물의 상태 등 견적 산출에 필요한 정보를 견적 산출 프롬프트에 입력할 수 있게 텍스트로 설명해.",
            },
            {
                "role": "user",
                "content": [
                    {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{base64_image}"}},
                    {"type": "text", "text": "면적과 탐지된 시설물, 시설물의 상태 등 견적 산출에 필요한 정보를 견적 산출 프롬프트에 입력할 수 있게 텍스트로 설명해. 면적은 제곱미터 단위로 수치화해서 제공해야 해."}
                ],
            },
        ],
        max_tokens=1000,
    )

    return response.choices[0].message.content