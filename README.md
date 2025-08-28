# APFM(AI Public Facilities Management) - 공공시설물 관리 AI 플랫폼
![Image](https://github.com/user-attachments/assets/bcb46d0f-78ab-42b7-9f48-6555a162341b)

## 서비스 소개
### 주요 서비스 내용
1. 공공시설물 자동 탐지
   * 기관이 보유한 CCTV 영상 데이터를 활용하여, YOLO 기반 객체 탐지 모델로 영상 속 시설물의 종류와 위치를 자동 식별한다​.
   * 사용 기술: YOLOv11
2. 공공시설물 방해도 분석
   * CCTV 영상에서 실시간 보행자 탐지 모델을 통해 시설물이 보행자 동선에 얼마나 간섭하는지를 평가한다​. 
   * 카메라 기준 보행자 밀집도, 시설물의 방해 정도, 보행자의 동선 변화 정도를 계산하여 방해도 점수를 산출한다.
   * 사용 기술: YOLOv11 다중 객체 추적 기술
3. 공공시설물 수리 견적 산출 (RAG 기반 LLM 활용)
   * 2025 건설공사 표준 품셈 문서 및 표준 시장 단가 문서를 검색·참조하여 수리 예상 견적과 견적 산출 이유를 제공한다.
   * 사용 기술: LangChain, RAG(Retrieval-Augmented Generation), 벡터 DB(ChromaDB)
4. 보고서 및 제안서 자동 생성
   * 시설물 데이터에서 예상 견적, 방해도 점수, 마지막 수리 날짜, 누적 민원 건수를 바탕으로 수리 우선순위를 산출한다.
   * 우선순위에 따른 수리를 제안하는 요약 보고서를 공문 형식에 맞추어 pdf 파일로 생성한다.
   * 수리가 필요한 시설물에 대하여 나라장터에 올릴 제안요청서 초안을 생성하여 사용자가 수정한 내용을 공문 형식에 맞추어 docx 파일로 생성한다.
   * 사용 기술: LangGraph + GPT-4 기반 LLM Agent
5. 주기적 모니터링
   * 사용자는 관리 주기를 설정할 수 있으며, 시스템은 해당 시점마다 자동으로 CCTV 영상을 분석한다.
   * kakao map API를 활용해 해당 CCTV 위치와 시설물 상태를 지도 위에 시각화하여 직관적으로 관리한다.
   * 사용 기술: FastAPI, kakao map API
  

### 목표 고객
공공시설물 유지관리 전문기관, 관련 지자체​


## 멤버 구성
* 윤원우: AI detection, Data Preprocessing, FastApi, FE
* 김예지: Estimate AI Agent, LLM LangGraph, FastApi, FE
* 오지민: Report AI Agent, LLM LangGraph, FastApi, FE
* 이창욱: AI Tracking, Data Preprocessing, FastApi, FE
* 이채현: Spring, DBA, CI/CD, Cloud
* 정민수: AI Detection, Spring, CI/CD, FE
* 한정민: Spring, CI/CD, Cloud, FE


## 프로젝트 기간
2025.07.07 - 2025.09.01


## 서비스 아키텍쳐


  
