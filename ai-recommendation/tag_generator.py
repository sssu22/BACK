from konlpy.tag import Komoran
from sklearn.feature_extraction.text import TfidfVectorizer

custom_compounds = [
    ('오픈', '소스', '오픈소스') #이런식으로 자주 나오는 합성명사를 커스텀 룰로 만들어서 추가 
]

# 여기엔 태그에서 제외할 단어들 적기
STOPWORDS = set([
    # 예: '대회', '참여', '최적화', '엄청난', '소개', '기술', ...
])

# 전역 변수로 Komoran 인스턴스 캐싱
_komoran = None

def get_komoran():
    """Komoran 최초 1회만 로드"""
    global _komoran
    if _komoran is None:
        print("[MODEL] Loading Komoran...")
        _komoran = Komoran()
    return _komoran


def merge_compounds(nouns):
    nouns_set = set(nouns)
    merged = nouns[:]
    for first, second, compound in custom_compounds:
        if first in nouns_set and second in nouns_set:
            merged = [w for w in merged if w != first and w != second]
            merged.append(compound)
    return merged



def extract_keywords(text, top_n=3):
#     komoran = Komoran()
    komoran = get_komoran()  # lazy load 사용
    nouns = komoran.nouns(text)
    nouns = [n for n in nouns if len(n) > 1 and n not in STOPWORDS]

    nouns = merge_compounds(nouns)
    if not nouns:
        return []
    docs = [' '.join(nouns)]
    vectorizer = TfidfVectorizer()
    tfidf = vectorizer.fit_transform(docs)
    words = vectorizer.get_feature_names_out()
    scores = tfidf.toarray()[0]
    top_indices = scores.argsort()[-top_n:][::-1]
    return [words[i] for i in top_indices]

# 테스트 용 main
if __name__ == "__main__":
    samples = [
        {
            "title": "인공지능 오픈소스 대회",
            "desc": "인공지능 기술을 활용한 오픈소스 소프트웨어 개발자들이 참여하는 대회가 개최됩니다."
        },
        {
            "title": "빅데이터 분석 방법론",
            "desc": "실무에서 활용되는 최신 빅데이터 분석 도구와 사례를 소개합니다."
        },
        {
            "title": "친환경 에너지 산업 트렌드",
            "desc": "태양광, 풍력 등 신재생에너지 산업의 최신 트렌드와 정부 정책 방향을 설명합니다."
        },
        {
            "title": "메타버스와 증강현실 기술",
            "desc": "메타버스 플랫폼, 증강현실 기기, 가상현실 생태계 등 차세대 기술 동향을 소개합니다."
        },
        {
            "title": "돈까스",
            "desc": "서울 돈까스 맛집은 맛나돈가스"
        },
        {
            "title": "더현대 백화점",
            "desc": "백화점 데이터에 최적화된 서울 여의도 백화점"
        }
    ]
    for i, sample in enumerate(samples, 1):
        text = f"{sample['title']} {sample['desc']}"
        tags = extract_keywords(text)
        print(f"예시 {i} | 추천 태그: {tags}")
