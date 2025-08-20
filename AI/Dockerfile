FROM python:3.11-slim

WORKDIR /app

# 필요한 시스템 패키지 설치
RUN apt-get update && apt-get install -y \
    git \
    libgl1 \
    libglib2.0-0 \
    ffmpeg \
    libjpeg-dev \
    zlib1g-dev \
    && rm -rf /var/lib/apt/lists/*

# 파이썬 패키지 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 앱 소스 복사
COPY . .

CMD ["uvicorn", "run:app", "--host", "0.0.0.0", "--port", "8000"]
