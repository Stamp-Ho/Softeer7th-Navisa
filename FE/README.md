# 🛫 Navisa (내비자)
> **복잡한 비자 신청의 모든 과정을 스마트하게**  
> 외국인과 전문 행정사를 잇는 **맞춤형 매칭 플랫폼**

<p align="center">
  <a href="https://www.navisa.site"><b>🛫 Navisa 바로가기</b></a> ·
  <a href="https://github.com/softeerbootcamp-7th/WEB-Team6-Navisa/wiki"><b>📖 통합 Wiki</b></a>
</p>

<p align="center">
  <img width="920" alt="Navisa Preview" src="https://github.com/user-attachments/assets/a5078b58-9ed2-485e-83ff-cfc7beb94720" />
</p>

---

## ✨ 서비스 핵심 기능

| 기능 | 설명 |
|---|---|
| **지능형 맞춤 행정사 추천** | LLM 임베딩 기반 **직무 유사도** + **활동성/평점**을 종합해 추천 |
| **필터 기반 탐색** | 직군, 지역, 국적, 언어 등 다양한 조건으로 행정사/외국인 탐색 |
| **실시간 1:1 채팅** | 웹소켓 기반 메시징, **수임 제안/수락 상태** 관리 |
| **비자 신청서 작성/편집** | 자동 저장, PDF 양식 매핑, **권한별 편집** |
| **리뷰/피드백** | 리뷰 점수 기반 추천 정밀도 개선 |
| **다국어 지원** | 한국어, 영어, 중국어, 일본어 |

> 더 자세한 내용은 **[📖 Navisa 통합 Wiki](https://github.com/softeerbootcamp-7th/WEB-Team6-Navisa/wiki)** 를 확인해주세요.

---

## 🧩 기술 스택

### Frontend
<p>
  <img src="https://img.shields.io/badge/Vite-9135FF?style=for-the-badge&logo=vite&logoColor=white" />
  <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black" />
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" />
</p>
<p>
  <img src="https://img.shields.io/badge/Tailwind%20CSS-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white" />
  <img src="https://img.shields.io/badge/TanStack%20Query-000000?style=for-the-badge&logo=tanstackquery&logoColor=white" />
  <img src="https://img.shields.io/badge/react--i18next-26A69A?style=for-the-badge&logo=i18next&logoColor=white" />
  <img src="https://img.shields.io/badge/React%20Hook%20Form-EC5990?style=for-the-badge&logo=reacthookform&logoColor=white" />
</p>

### Tech Spec (요약)
| 기술 | 구분 | 역할 |
| --- | --- | --- |
| **Vite** | Build Tool | 빠른 HMR을 통한 개발 피드백 속도 확보 |
| **TypeScript** | Language | 복잡한 비자 신청서 데이터 규격의 안정성 및 자동완성 지원 |
| **TanStack Query** | Server State | 서버 데이터 캐싱, Stale 관리, GC 자동화 |
| **Context** | UI State | 클라이언트 전역 상태 관리 |
| **React Router** | Routing | 뷰 전환 히스토리 관리 및 레이아웃 분리 |
| **Tailwind CSS** | Styling | 문서 편집 툴의 세밀한 UI 구현 및 디자인 시스템 구축 |
| **react-i18next** | i18n | 다국어 지원, 언어 감지 및 국제화 표준 규격 준수 |
| **pdf-lib** | PDF Engine | 기존 양식에 이미지, 텍스트를 더해 PDF 생성 |

---

## 🗂️ 디렉토리 구조

```bash
src/
├── api/                          # API 관련 파일
│   ├── fetchHooks/               # 파일 업로드 관련 hooks
│   ├── mutations/                # React Query mutations
│   ├── queries/                  # React Query queries
│   ├── services/                 # API 서비스
│   ├── types/                    # API 관련 타입
│   └── websocket/                # WebSocket 관련
│
├── assets/                       # SVG, 아이콘 등 UI 컴포넌트
├── components/                   # React 컴포넌트 (common / domain / form / layout)
├── constants/                    # 상수 정의 (job, language, nations, regions)
├── contexts/                     # React Context (Auth, Locale, WebSocket)
├── hooks/                        # 커스텀 Hooks
├── i18n/                         # 국제화(i18n) 설정 및 locale 리소스
├── pages/                        # 페이지 컴포넌트 (Chat/Documents/Landing/Onboard/Profile/Search)
├── styles/                       # 전역 스타일
├── types/                        # TypeScript 타입 정의
├── utils/                        # 유틸리티 함수
│
├── App.tsx
├── main.tsx
└── index.css
```

---

## 👩‍💻 FE 개발자

<p align="center">
  <table align="center">
    <tr>
      <td align="center">
        <img width="120" src="https://github.com/Stamp-Ho.png" alt="Stamp-Ho"/>
        <br />
        <b><a href="https://github.com/Stamp-Ho">정인호</a></b>
      </td>
      <td align="center">
        <img width="120" src="https://github.com/roony1225.png" alt="roony1225"/>
        <br />
        <b><a href="https://github.com/roony1225">연승환</a></b>
      </td>
    </tr>
  </table>
</p>
