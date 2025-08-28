# 1. Python 환경 + requirements 설치 (캐시 활용)
FROM python:3.11-slim AS base
WORKDIR /app

# requirements만 먼저 복사
COPY requirements.txt .

RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    && rm -rf /var/lib/apt/lists/* \
    && pip install --upgrade pip \
    && pip install --prefer-binary -r requirements.txt


# 2. 런타임 환경
FROM python:3.11-slim
WORKDIR /app

# 런타임에 필요한 OS 패키지 설치
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    libgl1 \
    libglib2.0-0 \
    ffmpeg \
    libjpeg-dev \
    zlib1g-dev \
    && rm -rf /var/lib/apt/lists/*

# base 단계에서 설치한 Python 패키지 복사
COPY --from=base /usr/local /usr/local

# 애플리케이션 코드 복사 (이 부분이 제일 자주 바뀜 → 캐시 영향 최소화)
COPY . .

# 모델 가중치는 별도로 복사 (코드와 따로 관리 가능)
COPY weights/v8/best.pt /app/weights/v8/best.pt
COPY weights/tracking/best.pt /app/weights/tracking/best.pt

EXPOSE 8080
CMD ["uvicorn", "run:app", "--host", "0.0.0.0", "--port", "8080", "--limit-max-request-size", "52428800"]
