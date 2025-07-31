import os
from dotenv import load_dotenv
from fastapi import FastAPI, Request, HTTPException
from openai import OpenAI

# .env 로부터 환경변수 로딩
load_dotenv()

# 환경변수에서 API 키 불러오기
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

app = FastAPI()

@app.post("/generate")
async def generate(request: Request):
    try:
        data = await request.json()
        query = data.get("query")
        

        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[
                {
                    "role": "user",
                    "content": query
                }
            ]
        )

        content = response.choices[0].message.content

        return {"content": content}

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error: {str(e)}")