## Team6-Navisa
# 🛫 Navisa (내비자)
**"복잡한 비자 신청의 모든 과정을 스마트하게"
외국인과 전문 행정사를 잇는 맞춤형 매칭 플랫폼**

<br>

# 👨‍👧‍👦 팀 소개: 6캔두잇
언어의 장벽과 복잡한 행정 절차 때문에 어려움을 겪는 외국인들이 한국에 안정적으로 정착할 수 있도록, <br>
IT 기술로 그 길을 밝히는 내비게이터 역할을 하고자 합니다.

효율적인 문서 자동화와 신뢰 기반의 행정사 매칭을 통해 비자 신청의 패러다임을 바꿉니다.

<p align="center">
    <table align="center">
        <tr>
            <th><a href="https://github.com/Stamp-Ho">정인호</a></th>
            <th><a href="https://github.com/roony1225">연승환</a></th>
            <th><a href="https://github.com/Hexeong">박인성</a></th>
            <th><a href="https://github.com/shinminkyoung1">신민경</a></th>
            <th><a href="https://github.com/JangIkhwan">장익환</a></th>
        </tr>
        <tr>
            <td><img width="150" src="https://github.com/Stamp-Ho.png"></td>
            <td><img width="150" src="https://github.com/roony1225.png"></td>
            <td><img width="150" src="https://github.com/Hexeong.png"></td>
            <td><img width="150" src="https://github.com/shinminkyoung1.png"></td>
            <td><img width="150" src="https://github.com/JangIkhwan.png"></td>
        </tr>
        <tr>
            <td align="center">FE</td>
            <td align="center">FE</td>
            <td align="center">BE</td>
            <td align="center">BE</td>
            <td align="center">BE</td>
        </tr>
    </table>
</p>

<br>

# 🔥 6캔두잇, 우리가 해낼 4가지 약속

- **[ALL-IN]** 중도 포기 없이 모든 기능을 스마트하게 마침표 찍기
- **[GROW-UP]** "성장은 기록으로부터", 인당 1개 이상의 기술 포스팅 달성하기
- **[DEEP-DIVE]** 겉핥기는 거부한다! 우리가 짠 모든 코드와 기술의 원리 파헤치기
- **[NO-LEAVE]** 뒤처지는 사람 없이, 우리 파트 지식은 우리가 상향 평준화하기

<br>



# 💕 그라운드 룰

0. **상호 신뢰, 원활한 협업 흐름 유지, 그리고 소중한 팀원의 시간 보호**
1. 요일마다 당번이 그날의 점심 메뉴를 정한다.
	- for 시간 효율, 각자 결정 장애 예방, 그리고 평등한 미식의 기회 ("아무거나" 금지 및 메뉴 선택의 독재 권력 부여)
2. 점심 먹고 나서 다 같이 체조(스쿼트)를 한다.
	- for 혈당 조절, 식곤증 타파, 그리고 건강한 개발 수명 연장 (거북목 방지 및 탄탄한 하체 보유 팀원 되기)
3. 언성이 높아지면 디저트를 먹으러 간다~!
	- for 심신 안정, 당 충전을 통한 평화 협정, 그리고 기분 전환 (싸움 구경보다 달콤한 케이크 구경)

<br>


# 🎨 기획&디자인 링크

[1️⃣ 기획 산출물 링크 [figma]](https://www.figma.com/design/AEMfiNWZUpZlpnAsM4ojmz/%EA%B8%B0%ED%9A%8D%EC%9D%98-%EB%B0%A9?node-id=31-692&t=k7xQxxNYoelwGRsR-1) <br>
[2️⃣ 디자인 산출물 링크 [figma]](https://www.figma.com/design/YHUKhgeoJpBeixaJUsV59V/%EB%94%94%EC%9E%90%EC%9D%B8%EC%9D%98-%EB%B0%A9?node-id=218-943&t=gp0JvzaDTZw5ry15-1)

<br>


# 📑 ERD 설계도
[Navisa ERD 주소](https://www.erdcloud.com/d/NiGGRPFFeqLzc8sLn)

<img width="1272" height="588" alt="스크린샷 2026-01-20 오후 2 52 03" src="https://github.com/user-attachments/assets/cbf9c5df-d06e-4845-a572-93e49cf2c481" />


<br>



# 🧗‍♀️ 백로그
[Navisa 백로그 [Notion]](https://www.notion.so/bside/2ed220202735807591f8cd19a8350dd7?source=copy_link)


<br>

# 👨‍🏫 브랜치 전략

- **Git Flow**를 기반으로 하며, 효율적인 이슈 관리를 위해 브랜치명을 이슈 번호와 연동합니다.
- **브랜치 종류 및 명명 규칙**
    - `main` : 우리가 최종 개발 시 Merge 하는 곳
    - `develop` : 개발 중 merge하는 최상위 브랜치
    - `태그/#이슈번호-기능명` : 기능을 개발하면서 각자가 사용할 브랜치
        
        ex. `feat/#1-kakao-oauth`
        
    - `hotfix` : 급한 수정사항 및 QA를 반영할 때 사용할 브랜치
    
- **브랜치 흐름도**
    
    ```jsx
    // 분기 그래프
    main
    	ㄴ develop
    		ㄴ 태그/#이슈번호-기능명
    ```
    
    ```jsx
    // 브랜치 전략 예시
    main
    	ㄴ develop
    		ㄴfeat/#1-kakao-oauth // 이슈 브랜치
    		ㄴrefactor/#2-login // 이슈 브랜치
    ```

<br>

# 🤙🏻 커밋 컨벤션

[자세한 6캔두잇의 협업 프로세스 [Notion]](https://www.notion.so/bside/6-2ed22020273580d99f1ed4906ce86e85?source=copy_link)

- **Squash & Merge** 방식을 채택하여 개별 커밋에서는 이슈 번호를 생략하고, 변경 대상(BE/FE)을 명시하여 직관성을 높였습니다.
- 기본적으로 다음 커밋 메시지 규칙을 따릅니다.
- **형식: `태그(변경 대상) : 메시지`**

| **태그이름** | **내용** |
| --- | --- |
| `feat` | 새로운 기능 (파일 추가도 포함)을 추가할 경우 |
| `refactor` | 코드 수정, 프로덕션 코드 리팩토링 |
| `fix` | 버그를 고친 경우 |
| `!HOTFIX` | 급하게 치명적인 버그를 고쳐야하는 경우 |
| `style` | 코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우 |
| `comment` | 필요한 주석 추가 및 변경 |
| `docs` | 문서를 수정한 경우 |
| `test` | 테스트 추가, 테스트 리팩토링(프로덕션 코드 변경 X) |
| `chore` | 빌드 테스트 업데이트, 패키지 매니저를 설정하는 경우(프로덕션 코드 변경 X) |
| `rename` | 파일 혹은 폴더명을 수정하거나 옮기는 작업만인 경우 |
| `remove` | 파일을 삭제하는 작업만 수행한 경우 |

```java
태그: 메시지

- 추가적인 설명들...

--------
// 설명

태그 : feat, chore 등등
메시지: 우리가 일반적으로 적는 내용
-----
// 예시

feat: 매칭 결과 API에 score 필드 추가
fix: 로그인 에러 메시지 표시 문제 수정
chore: cd 워크플로우 수정
```


<br>

# 📅 데일리 노트
[6캔두잇의 데일리 노트 [Notion]](https://www.notion.so/bside/2ee22020273580e58bdacc148741eaec?source=copy_link)

<br>

<br>


