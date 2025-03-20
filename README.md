# 📸 Patata: 사진 스팟의 모든 것  
![파타타_홍보영상](https://github.com/user-attachments/assets/1e9bba47-932b-46f7-926f-d50eff487e03)

[📲 Play Store 다운로드](https://play.google.com/store/apps/details?id=com.cmc.patata)  
🎥 [홍보 영상 보기](https://drive.google.com/file/d/1H8I9qslE6addU-GcmiDvOxyeULCEUye1/view?usp=sharing)


## **📌 프로젝트 소개**
**Patata**는 위치 기반 **사진 명소 공유 앱**입니다.  
여행지에서 멋진 풍경을 찾고 싶을 때, 일상 속 특별한 장소를 발견하고 싶을 때, **Patata가 당신을 도와드립니다.**  

- **🗺️ 위치 기반 서비스**: 사용자 주변의 사진 스팟을 탐색  
- **🏆 사용자 추천**: 인기 스팟 및 추천 장소 제공  
- **📷 스팟 등록**: 사용자가 직접 촬영한 사진과 함께 명소 등록  
- **💬 리뷰 및 스팟 평가**: 방문 후 리뷰 작성 및 좋아요 기능  
- **🔍 필터링 및 검색**: 특정 카테고리나 해시태그로 스팟 찾기  

---

## **👨‍💻 팀원 구성**
| 역할  | 이름  |
|------|------|
| 기획  | 제리  |
| 디자인 | 행  |
| Android | 짐 (전병선) |
| iOS  | 멜론 |
| 서버  | 도얌 |

---

## **⚙️ 개발 환경 및 기술 스택**
- **개발 환경**: Android Studio, Kotlin, XML
- **아키텍처**: MVVM, Clean Architecture, Multi Module
- **비동기 처리**: Coroutine, Flow
- **DI**: Hilt
- **네트워크**: Retrofit2, OkHttp
- **지도 서비스**: Naver Map SDK
- **이미지 처리**: Glide

---

## **📂 프로젝트 구조**
Patata_AOS
```
├── app
│   ├── navigation
├── common
│   ├── util
│   ├── extensions
├── data
│   ├── model
│   ├── remote
│   ├── repository
│   ├── local
├── design
│   ├── component
├── domain
│   ├── model
│   ├── usecase
│   ├── viewmodel
├── presentation
│   ├── ui
│   ├── viewmodel
│   ├── navigation
│   ├── di
```

---

## **📆 개발 기간**
📅 **2025-01-07 ~ 2025-02-28** (총 2개월)

---


## **🚀 기술적 도전 및 문제 해결**

1. 멀티 모듈 & Single Activity 내 Navigation 적용

	문제: 기존 프로젝트에서는 단일 모듈에서 Navigation을 관리했지만, 이번 프로젝트에서는 Multi-Module 구조를 적용하면서 Navigation의 간소화 및 의존성 관리가 중요한 과제가 되었다.
해결: MainActivity에서 BottomNavigationView를 관리하고, 각 기능별 Feature Module에서 Fragment를 제공하는 방식으로 구성했다. 모듈 간 의존성을 줄이기 위해 GlobalNavigation 인터페이스를 활용하여 MainActivity에서 Navigation을 수행하도록 구현했다.

🔹 해결 과정
	•	NavHostFragment와 Navigation Component의 동작을 심층적으로 분석하고, Single Activity 구조에서 Fragment 간 최적화된 이동 방식을 학습.
	•	멀티 모듈 환경에서도 DeepLink를 활용하여 모듈 간 화면 이동을 유연하게 처리하는 방식을 적용.

✅ 결과
	•	모듈 간 의존성을 줄이면서도 유연한 Navigation 구조를 설계할 수 있었다.
	•	Fragment 간 이동 시 Navigation 재사용성을 극대화하고 유지보수성을 높이는 데 성공했다.

⸻

2. Google OAuth 서명 누락으로 인한 로그인 오류

	문제: Play Store에 배포 후, SHA-1 서명 누락으로 인해 Google OAuth 인증이 실패하는 문제가 발생했다.
해결: Google Play Console에서 앱 서명을 등록하여 OAuth 인증 문제를 해결했다.

🔹 문제 해결 과정
	1.	로컬 환경에서는 debug.keystore, release.keystore를 등록했기 때문에 문제가 발생하지 않았고, 배포 후에만 인증 오류가 발생.
	2.	초기에는 코드 문제, 기기 호환성 이슈, 네트워크 문제 등을 의심하며 다양한 테스트를 진행했으나 원인을 찾지 못함.
	3.	분석 끝에, 배포 버전에서는 Google Play의 앱 서명을 별도로 등록해야 한다는 점을 인식.
	4.	Play Store 서명을 GCP에 추가 등록하여 OAuth 인증 문제를 해결.

✅ 결과 및 배운 점
	•	배포 환경에서는 Play Store 서명을 고려한 OAuth 설정이 필요함을 명확히 이해함.
	•	배포 후 발생하는 문제를 방지하기 위해 CI/CD에서 Play Store 서명을 자동으로 등록하는 방안도 검토해야 함을 깨달음.

⸻

3. 느린 이미지 로딩 최적화

	문제: 고해상도 이미지를 직접 불러올 때 로딩 시간이 길어 사용자 경험이 저하됨.
해결: 이미지 크기 최적화 및 Glide의 thumbnail 기능을 활용하여 속도를 개선했다.

🔹 해결 과정
	1.	이미지 크기 자체를 최적화
	•	모바일에서 차이가 거의 없는 선에서 용량을 줄이기 위해 다양한 시도를 거침.
	•	최적의 비율을 찾아 이미지 크기 20% 감소, 퀄리티 60% 감소.
	•	이를 통해 로딩 속도를 3초 → 0.7초로 개선하면서도 사용자 경험 유지.
	2.	네트워크 성능 테스트 및 클라우드 최적화
	•	기존에 Amazon S3 한국 리전을 사용했으나, 네이버 클라우드가 20% 더 빠른 응답 속도를 보임.
	•	네이버 클라우드 스토리지를 테스트하여 보다 빠른 데이터 전송 방식을 검토.

✅ 결과 및 배운 점
	•	단순히 Glide의 캐싱을 활용하는 것보다 네트워크 성능 분석을 통해 최적화하는 것이 더 효과적임을 경험.
	•	클라우드 서비스 간 성능 비교 및 최적의 선택이 중요한 의사결정 요소임을 깨달음.

---

## **📌 주요 기능**
| 기능  | 설명  |
|------|------|
| 🏠 홈 | 추천 명소, 인기 스팟 제공 |
| 📍 지도 | 주변 스팟 탐색 및 상세 정보 확인 |
| 🔍 검색 | 키워드 및 해시태그로 스팟 검색 |
| 📸 스팟 등록 | 위치 및 사진을 추가하여 스팟 등록 |
| 💬 리뷰 | 스팟 방문 후 후기 및 평가 작성 |
| ❤️ 즐겨찾기 | 관심 있는 스팟을 스크랩 |

---
