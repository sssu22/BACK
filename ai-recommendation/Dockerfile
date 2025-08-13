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

FROM python:3.10-slim-bullseye

WORKDIR /app
ENV DEBIAN_FRONTEND=noninteractive
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:$PATH"

# 필수 패키지 설치 (OpenJDK 포함)
RUN apt-get clean && apt-get update -y --fix-missing && \
    apt-get install -y --no-install-recommends \
        openjdk-11-jre-headless \
        libgomp1 \
        gcc \
        python3-dev \
        libopenblas-dev \
        curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 종속성 먼저 복사해서 캐시 활용
COPY requirements.txt /app/requirements.txt
RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# 나머지 앱 복사
COPY . /app

EXPOSE 8000
CMD ["uvicorn", "api:app", "--host", "0.0.0.0", "--port", "8000"]
