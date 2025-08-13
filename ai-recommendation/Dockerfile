## Dockerfile
#FROM python:3.10-bullseye
#WORKDIR /app
#
#ENV DEBIAN_FRONTEND=noninteractive
#
#RUN apt-get update \
#  && apt-get install -y --no-install-recommends openjdk-11-jre-headless \
#  && apt-get clean \
#  && rm -rf /var/lib/apt/lists/*
#
#COPY . /app
#
#RUN pip install --upgrade pip
#RUN pip install -r requirements.txt
#
#ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
#ENV PATH="$JAVA_HOME/bin:$PATH"
#
#EXPOSE 8000
#
#CMD ["uvicorn", "api:app", "--host", "0.0.0.0", "--port", "8000"]
# FastAPI + Komoran(JVM) + Prophet/LightFM 설치 최적화
FROM python:3.10-slim

ENV DEBIAN_FRONTEND=noninteractive \
    PYTHONUNBUFFERED=1

WORKDIR /app

# ===== 런타임 필수 패키지 (지워지면 안 됨) =====
# - openjdk-11-jre-headless : konlpy Komoran 실행용 JVM
# - libgomp1 : prophet 등에서 필요
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        openjdk-11-jre-headless \
        libgomp1 \
        curl && \
    rm -rf /var/lib/apt/lists/*

# ===== 빌드 전용 패키지 (pip 설치에만 쓰고 제거) =====
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        build-essential \
        gcc \
        g++ \
        git && \
    rm -rf /var/lib/apt/lists/*

# pip 업그레이드 + 요구사항 설치 (캐시 미보관)
COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r /app/requirements.txt

# 빌드툴 제거로 이미지 슬림화
RUN apt-get purge -y build-essential gcc g++ git && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

# 애플리케이션 복사
COPY . /app

# JVM 경로 설정 (OpenJDK 11)
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:$PATH"

EXPOSE 8000

CMD ["uvicorn", "api:app", "--host", "0.0.0.0", "--port", "8000"]
