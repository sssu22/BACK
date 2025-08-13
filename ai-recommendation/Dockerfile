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
# ---- base: slim + 필수 런타임 ----
FROM python:3.10-slim-bullseye

WORKDIR /app
ENV DEBIAN_FRONTEND=noninteractive \
    PYTHONUNBUFFERED=1 \
    JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 \
    PATH="$JAVA_HOME/bin:$PATH"

# 런타임 패키지 (지속 필요)
# - openjdk-11-jre-headless : konlpy(JPype)용 JVM
# - libgomp1 : prophet 등에서 사용
# - libopenblas-dev : lightfm/넘파이/사이파이 BLAS
# - curl : 디버깅/헬스체크 등
RUN apt-get update && apt-get install -y --no-install-recommends \
    openjdk-11-jre-headless \
    libgomp1 \
    libopenblas-dev \
    curl \
 && rm -rf /var/lib/apt/lists/*

# 빌드 전용 패키지 (pip 설치 시에만 사용 후 제거)
# - build-essential, gcc, g++ : lightfm/prophet 컴파일
# - git : 일부 패키지가 내부적으로 필요할 때가 있음
# - python3-dev : 파이썬 C 확장 빌드 헤더
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential gcc g++ git python3-dev \
 && rm -rf /var/lib/apt/lists/*

# pip 최신화
RUN pip install --no-cache-dir --upgrade pip

# ✅ CPU 전용 PyTorch를 "먼저" 설치 (CUDA 패키지 끌려오는 것 방지)
#    requirements.txt 에서는 torch를 제거해 두세요!
RUN pip install --no-cache-dir --index-url https://download.pytorch.org/whl/cpu \
    torch==2.4.0+cpu

# 앱 의존성 설치
COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r /app/requirements.txt

# 빌드 툴 제거로 이미지 슬림화
RUN apt-get purge -y build-essential gcc g++ git python3-dev \
 && apt-get autoremove -y \
 && rm -rf /var/lib/apt/lists/*

# 애플리케이션 복사
COPY . /app

EXPOSE 8000
CMD ["uvicorn", "api:app", "--host", "0.0.0.0", "--port", "8000"]
