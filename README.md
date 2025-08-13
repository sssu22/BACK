# 🐥 Trendlog
<!--
**Trendlog**는 사용자가 선택한 트렌드와 개인 경험 데이터를 기반으로, 인공지능이 사회 전반의 트렌드 흐름을 분석하고 맞춤형 추천 및 시계열 예측을 제공하는 **오픈소스 플랫폼**입니다.  

트렌드와 관련된 **외부 데이터·검색량·사용자 활동**을 수집·가공하여 트렌드 순위와 개인화 추천을 제공하며, 사용자는 자신의 관심사가 어떤 사회적 흐름 속에 있는지 **한눈에 파악**할 수 있습니다.
-->

오늘날 사람들은 SNS, 검색, 콘텐츠 소비를 통해 자신만의 관심사와 트렌드 경험을 쌓지만, 이를 정량적·시각적으로 이해할 수 있는 도구는 부족합니다. 이러한 한계를 해결하고자 **Trendlog**는 사용자가 겪은 최초 경험을 입력하고, 직접 연관된 트렌드 키워드를 함께 선택해 업로드할 수 있는 구조로 설계되었습니다.

이렇게 수집된 데이터는  **외부 데이터·검색량·사용자 활동**과 결합되어 사회 전반의 트렌드 흐름을 분석하며, 맞춤형 추천과 시계열 예측을 제공하는 오픈소스 플랫폼으로 발전합니다. 이 플랫폼은 트렌드 순위와 개인화 추천을 한눈에 보여주어, 사용자가 자신의 관심사가 어떤 사회적 흐름 속에 있는지 쉽게 파악할 수 있도록 돕습니다.


> 개발 기간: 2025.07.04 ~ 2025.08.17
</br>

## 👩‍💻 팀원 소개
| <img src="https://github.com/nenini.png" width="120"/> | <img src="https://github.com/kkshyun.png" width="120"/> |
|:---:|:---:|
| **고예린** / Backend Developer | **김세현** / Backend Developer |
| [@nenini](https://github.com/nenini) | [@kkshyun](https://github.com/kkshyun) |
</br>

## ⚙️ 주요 기능

### 1. 인증 & 사용자 관리
- **JWT 기반 로그인/회원가입**  
- **GCS 이미지 업로드**: 프로필 이미지 저장  
- **비밀번호 재설정**: 이메일 링크 기반 웹 페이지 제공  

### 2. 검색
- **QueryDSL 기반 키워드 검색**: 트렌드, 게시글, 태그, 나의 활동 등 통합 검색 지원

### 3. 게시글
- **위치 연동**: Kakao Map API 기반 좌표/주소 등록  
- **인기 태그**: 태그 사용량 통계 기반 인기 키워드 제공  

### 4. 트렌드 분석 & 추천
- **뉴스 데이터 수집**: Naver News API로 최신 뉴스 URL 생성  
- **유사 트렌드 추천**: OpenAI API 기반 태그 및 연관 트렌드 생성  
- **유튜브 언급량 분석**: YouTube Search API로 관심 키워드 트렌드 확인  
- **트렌드 추천**: LightFM 모델 기반 개인 맞춤형 추천  
- **트렌드 예측**: Prophet 시계열 예측 모델로 향후 트렌드 흐름 예측  
- **트렌드 점수화**: 검색량, 게시글 인기, AI 뉴스 점수를 종합하여 산출
- **피크타임 분석**: 트렌드 인기 시기 분석

</br>


<!--
## 🚀 배포 링크
- **Production**: [https://yourdomain.com](https://yourdomain.com)
- **Swagger**: [https://yourdomain.com/swagger-ui/index.html](https://yourdomain.com/swagger-ui/index.html)
---
-->


## 🛠 기술 스택

### Backend
<p>
<img src="https://img.shields.io/badge/-Java-white?style=flat&logo=Java&logoColor=007396">
<img src="https://img.shields.io/badge/-Spring%20Boot-white?style=flat&logo=Spring%20Boot&logoColor=6DB33F">
<img src="https://img.shields.io/badge/-Spring%20Security-white?style=flat&logo=Spring%20Security&logoColor=6DB33F">
<img src="https://img.shields.io/badge/-JPA-white?style=flat&logo=Hibernate&logoColor=59666C">
<img src="https://img.shields.io/badge/-QueryDSL-white?style=flat&logoColor=009688">
<img src="https://img.shields.io/badge/-JWT-white?style=flat&logo=jsonwebtokens&logoColor=000000">
</p>

### Database & Infra
<p>
<img src="https://img.shields.io/badge/-PostgreSQL-white?style=flat&logo=PostgreSQL&logoColor=4169E1">
<img src="https://img.shields.io/badge/-Redis-white?style=flat&logo=Redis&logoColor=DC382D">
<img src="https://img.shields.io/badge/-Docker-white?style=flat&logo=Docker&logoColor=2496ED">
<img src="https://img.shields.io/badge/-AWS-white?style=flat&logo=Amazon%20AWS&logoColor=FF9900">
<img src="https://img.shields.io/badge/-AWS%20EC2-white?style=flat&logo=Amazon%20EC2&logoColor=FF9900">
<img src="https://img.shields.io/badge/-Google%20Cloud-white?style=flat&logo=Google%20Cloud&logoColor=4285F4">
</p>

### AI & ML
<p>
<img src="https://img.shields.io/badge/-Prophet-white?style=flat&logoColor=025E8C">
<img src="https://img.shields.io/badge/-LightFM-white?style=flat&logoColor=FF6F00">
<img src="https://img.shields.io/badge/-sentence--transformers-white?style=flat&logo=PyTorch&logoColor=EE4C2C">
<img src="https://img.shields.io/badge/-Komoran-white?style=flat&logoColor=008000">
<img src="https://img.shields.io/badge/-FastAPI-white?style=flat&logo=FastAPI&logoColor=009688">
<img src="https://img.shields.io/badge/-SQLAlchemy-white?style=flat&logoColor=D71F00">
</p>

### API
<p>
<img src="https://img.shields.io/badge/-OpenAI-white?style=flat&logo=OpenAI&logoColor=412991">
<img src="https://img.shields.io/badge/-Naver%20News%20API-white?style=flat&logo=Naver&logoColor=03C75A">
<img src="https://img.shields.io/badge/-YouTube%20Search%20API-white?style=flat&logo=YouTube&logoColor=FF0000">
<img src="https://img.shields.io/badge/-Kakao%20Map%20API-white?style=flat&logo=Kakao&logoColor=FFCD00">
<img src="https://img.shields.io/badge/-Swagger-white?style=flat&logo=Swagger&logoColor=85EA2D">
</p>

### Collaboration & Project Management
<p>
<img src="https://img.shields.io/badge/-GitHub-white?style=flat&logo=GitHub&logoColor=181717">
<img src="https://img.shields.io/badge/-GitHub%20Issues-white?style=flat&logo=GitHub&logoColor=181717">
<img src="https://img.shields.io/badge/-Notion-white?style=flat&logo=Notion&logoColor=000000">
<img src="https://img.shields.io/badge/-Discord-white?style=flat&logo=Discord&logoColor=5865F2">
</p>


<!--
### Backend
<p>
<img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=Java&logoColor=white">
<img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white">
<img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=Spring%20Security&logoColor=white">
<img src="https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=Hibernate&logoColor=white">
<img src="https://img.shields.io/badge/QueryDSL-009688?style=for-the-badge">
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white">
</p>

### Database & Infra
<p>
<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=PostgreSQL&logoColor=white">
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white">
<img src="https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=Amazon%20AWS&logoColor=white">
<img src="https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=Amazon%20EC2&logoColor=white">
<img src="https://img.shields.io/badge/Google%20Cloud-4285F4?style=for-the-badge&logo=Google%20Cloud&logoColor=white">
</p>

### AI & ML
<p>
<img src="https://img.shields.io/badge/Prophet-025E8C?style=for-the-badge&logoColor=white">
<img src="https://img.shields.io/badge/LightFM-FF6F00?style=for-the-badge&logoColor=white">
<img src="https://img.shields.io/badge/sentence--transformers-2F2F2F?style=for-the-badge&logo=PyTorch&logoColor=white">
<img src="https://img.shields.io/badge/Komoran-008000?style=for-the-badge&logoColor=white">
<img src="https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=FastAPI&logoColor=white">
<img src="https://img.shields.io/badge/SQLAlchemy-D71F00?style=for-the-badge&logoColor=white">
</p>

### API
<p>
<img src="https://img.shields.io/badge/OpenAI-412991?style=for-the-badge&logo=OpenAI&logoColor=white">
<img src="https://img.shields.io/badge/Naver%20News%20API-03C75A?style=for-the-badge&logo=Naver&logoColor=white">
<img src="https://img.shields.io/badge/YouTube%20Search%20API-FF0000?style=for-the-badge&logo=YouTube&logoColor=white">
<img src="https://img.shields.io/badge/Kakao%20Map%20API-FFCD00?style=for-the-badge&logo=Kakao&logoColor=black">
<img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=black">
</p>

### 협업 & 관리
<p>
<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white">
<img src="https://img.shields.io/badge/GitHub%20Issues-181717?style=for-the-badge&logo=GitHub&logoColor=white">
<img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white">
<img src="https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=Discord&logoColor=white">
</p>
-->

</br>

## 📂 프로젝트 구조
```plaintext
├── Dockerfile.spring
├── ai-recommendation/              # Python 기반 추천/예측 스크립트
│   ├── Dockerfile
│   ├── api.py
│   ├── recommend.py
│   ├── recommend_news.py
│   ├── recommend_trends_time_series.py
│   ├── tag_generator.py
│   ├── trend_recommender.py
│   ├── all_trends.csv
│   ├── predicted_top3.csv
│   ├── recommended_trends.csv
│   ├── trend_recommend_scores.csv
│   ├── trend_score.csv
│   └── requirements.txt
├── docker-compose.yml
├── docker-compose.prod.yml
├── src
│   ├── main
│   │   ├── java/com/example/trendlog
│   │   │   ├── controller/
│   │   │   ├── domain/
│   │   │   │   ├── post/
│   │   │   │   ├── trend/
│   │   │   │   └── user/
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── auth/
│   │   │   │   │   ├── password/
│   │   │   │   │   ├── post/
│   │   │   │   │   ├── trend/
│   │   │   │   │   └── user/
│   │   │   │   └── response/
│   │   │   │       ├── auth/
│   │   │   │       ├── common/
│   │   │   │       ├── mypage/
│   │   │   │       ├── post/
│   │   │   │       ├── trend/
│   │   │   │       └── user/
│   │   │   ├── global/
│   │   │   │   ├── config/
│   │   │   │   ├── docs/
│   │   │   │   ├── dto/
│   │   │   │   ├── exception/
│   │   │   │   │   ├── code/
│   │   │   │   │   ├── common/
│   │   │   │   │   ├── trend/
│   │   │   │   │   └── user/
│   │   │   │   └── security/
│   │   │   │       ├── jwt/
│   │   │   │       └── userdetails/
│   │   │   ├── repository/
│   │   │   │   ├── post/
│   │   │   │   ├── trend/
│   │   │   │   └── user/
│   │   │   └── service/
│   │   │       ├── external/
│   │   │       ├── post/
│   │   │       ├── trend/
│   │   │       └── user/
│   │   └── resources
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-prod.yml
│   │       └── gcp-service-key.json
