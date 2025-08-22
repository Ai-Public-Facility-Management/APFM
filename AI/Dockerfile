# fastapi/Dockerfile
FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
COPY weights/yolo/v8/best.pt /app/weights/yolo/v8/best.pt
EXPOSE 8000
CMD ["uvicorn", "estimate:app", "--host", "0.0.0.0", "--port", "8000"]
