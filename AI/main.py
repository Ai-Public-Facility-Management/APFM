from fastapi import FastAPI, UploadFile, File, HTTPException
import os
from dotenv import load_dotenv
from graph import graph  # graph.py에서 정의한 graph 객체
import shutil
from langchain_community.vectorstores import Chroma
from langchain.embeddings import HuggingFaceEmbeddings  # ✅ 변경된 임베딩

# .env 로부터 환경변수 로딩
load_dotenv()

app = FastAPI()

@app.post("/estimate")
async def estimate(image: UploadFile = File(...)):
    try:
        # 1. 이미지 저장
        os.makedirs("temp", exist_ok=True)
        image_path = os.path.join("temp", image.filename)
        with open(image_path, "wb") as buffer:
            shutil.copyfileobj(image.file, buffer)

        # 2. ChromaDB 불러오기 - 기존 768차원 모델 사용
        embedding_model = HuggingFaceEmbeddings(model_name="jhgan/ko-sbert-nli")
        vectordb = Chroma(
            persist_directory="./chroma",  # chroma DB 경로
            embedding_function=embedding_model
        )

        # 3. 그래프 실행
        state = {
            "image_path": image_path,
            "vectordb": vectordb
        }

        result = graph.invoke(state)

        # 4. 결과 반환
        return {
            "vision_analysis": result.get("vision_analysis"),
            "cost_estimate": result.get("cost_estimate")
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))