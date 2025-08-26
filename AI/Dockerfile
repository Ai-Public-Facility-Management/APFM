FROM python:3.11-slim AS base
WORKDIR /app

COPY requirements.txt .


RUN apt-get update && apt-get install -y git \
    && rm -rf /var/lib/apt/lists/*

RUN pip install --upgrade pip \
    && pip install --prefer-binary -r requirements.txt

FROM python:3.11-slim

RUN apt-get update && apt-get install -y \
    git \
    libgl1 \
    libglib2.0-0 \
    ffmpeg \
    libjpeg-dev \
    zlib1g-dev \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy Python deps from base stage
COPY --from=base /usr/local /usr/local

# Copy app code
COPY . .
COPY weights/yolo/v8/best.pt /app/weights/yolo/v8/best.pt

EXPOSE 8080
CMD ["uvicorn", "run:app", "--host", "0.0.0.0", "--port", "8080"]
