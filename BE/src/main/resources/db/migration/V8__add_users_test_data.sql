-- 1. FOREIGNER 타입 (UNFILLED_FOREIGNER 20명)
INSERT INTO users (user_id, email, password_hash, user_type, login_type, is_verified, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'foreigner_unfilled_' || n || '@navisa.com',
    '$2a$10$Wd3xWkEvvT9.K9ddtCCoHe1Ue9T9.mey59zz2LnMewxz1y8nY4mHC',
    'UNFILLED_FOREIGNER',
    'EMAIL',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 20) AS n;

-- 2. FOREIGNER 타입 (FILLED_FOREIGNER 20명)
INSERT INTO users (user_id, email, password_hash, user_type, login_type, is_verified, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'foreigner_filled_' || n || '@navisa.com',
    '$2a$10$Wd3xWkEvvT9.K9ddtCCoHe1Ue9T9.mey59zz2LnMewxz1y8nY4mHC',
    'FILLED_FOREIGNER',
    'EMAIL',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 20) AS n;

-- 3. AGENT 타입 (INVALID_AGENT 20명)
INSERT INTO users (user_id, email, password_hash, user_type, login_type, is_verified, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'agent_invalid_' || n || '@navisa.com',
    '$2a$10$Wd3xWkEvvT9.K9ddtCCoHe1Ue9T9.mey59zz2LnMewxz1y8nY4mHC',
    'INVALID_AGENT',
    'EMAIL',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 20) AS n;

-- 4. AGENT 타입 (VALID_AGENT 20명)
INSERT INTO users (user_id, email, password_hash, user_type, login_type, is_verified, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'agent_valid_' || n || '@navisa.com',
    '$2a$10$Wd3xWkEvvT9.K9ddtCCoHe1Ue9T9.mey59zz2LnMewxz1y8nY4mHC',
    'VALID_AGENT',
    'EMAIL',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 20) AS n;

-- 5. FILLED_FOREIGNER 타입 유저(20명)에 대한 프로필 생성
INSERT INTO foreigner_profile (foreigner_id, user_id, nickname, status, last_login_at, created_at, updated_at)
SELECT
    gen_random_uuid(),
    u.user_id,
    (ARRAY['신비한', '행복한', '차분한', '친절한', '용감한'])[floor(random() * 5 + 1)] || '_' || substring(gen_random_uuid()::text, 1, 4),
    'IDLE',
    NOW(),
    NOW(),
    NOW()
FROM users u
WHERE u.user_type = 'FILLED_FOREIGNER' AND u.email LIKE 'foreigner_filled_%';

-- 5-1. 생성된 20명 중 15명만 REQUESTING 상태로 변경
WITH targets AS (
    SELECT foreigner_id
    FROM foreigner_profile
    WHERE status = 'IDLE'
      AND user_id IN (SELECT user_id FROM users WHERE email LIKE 'foreigner_filled_%')
    LIMIT 15
    )
UPDATE foreigner_profile
SET status = 'REQUESTING', updated_at = NOW()
WHERE foreigner_id IN (SELECT foreigner_id FROM targets);

-- 6. 외국인 구사 언어(Languages) 매핑
INSERT INTO foreigner_language (foreigner_id, language_id, created_at, updated_at)
SELECT
    p.foreigner_id,
    l.language_id,
    NOW(),
    NOW()
FROM foreigner_profile p
         JOIN users u ON p.user_id = u.user_id
         CROSS JOIN LATERAL (
    SELECT language_id
    FROM language
    WHERE p.foreigner_id IS NOT NULL
    ORDER BY random()
        LIMIT 1
) l
WHERE u.email LIKE 'foreigner_filled_%';

-- 7. 외국인 국적(Nationalities) 매핑
INSERT INTO foreigner_nationality (foreigner_id, nationality_id, created_at, updated_at)
SELECT
    p.foreigner_id,
    n_rand.nationality_id,
    NOW(),
    NOW()
FROM foreigner_profile p
         JOIN users u ON p.user_id = u.user_id
         CROSS JOIN LATERAL (
    SELECT nationality_id
    FROM nationality
    WHERE p.foreigner_id IS NOT NULL
    ORDER BY random()
        LIMIT 1
) n_rand
WHERE u.email LIKE 'foreigner_filled_%';

-- 8. VALID_AGENT 프로필 생성 (20명)
INSERT INTO agent_profile (
    agent_id, user_id, agent_name, agent_birth, profile_object_key,
    agent_business_time, office_name, office_address, detail_address,
    additional_history, phone_number, license_no, license_issued_at,
    license_inner_page_no, license_management_no, agent_comment,
    active_score, created_at, updated_at
)
SELECT
    gen_random_uuid(),
    u.user_id,
    '행정사_' || n.num,
    '1980-01-01'::DATE + (n.num * interval '1 year'),
    (ARRAY[
         'agent-profile/origin/006155eb-4f1c-4e7c-b65f-f7a33dba3acb.png',
     'agent-profile/origin/00754583-9fa8-4142-889c-298db6f30a10.png',
     'agent-profile/origin/05772e37-d4ea-4daa-a984-7e9752726c2b.jpeg',
     'agent-profile/origin/08194586-6aa7-4a89-bf08-9a44b43a2129.jpeg',
     'agent-profile/origin/08402aa7-9820-4234-91bc-881308e34c86.jpeg',
     'agent-profile/origin/0a546af5-8a7c-4d22-89cb-f99a114ebca1.png',
     'agent-profile/origin/0ccf1e60-c0fb-47c8-8acf-2c987562c821.jpeg',
     'agent-profile/origin/0d12b049-6fdf-420d-bc2f-08dcce29fc06.png',
     'agent-profile/origin/11511b82-9c53-406f-bade-6d612ce4edb3.jpeg',
     'agent-profile/origin/12f6f820-9b7d-4d91-9d6e-2d08b720bbcc.jpeg',
     'agent-profile/origin/15f3d886-5d4b-4eed-8f43-af77d489f899.png',
     'agent-profile/origin/1768494c-427d-4adc-8c36-a20ec5232cb4.png',
     'agent-profile/origin/188af22a-6bbe-4a2d-ae79-b1926958976c.png',
     'agent-profile/origin/1a651c59-4e9c-4739-8be7-d9077dc795b8.jpeg',
     'agent-profile/origin/1ca57901-ec36-451a-9525-6b0d0da9b900.png',
     'agent-profile/origin/2d21c46d-05a3-498d-aa2d-7825506c8698.png',
     'agent-profile/origin/3022cd41-20d8-4adf-ac97-3c7b29451787.png',
     'agent-profile/origin/34044fce-e4fc-4242-ac82-05f99d750078.png',
     'agent-profile/origin/369e1c66-2fec-4a00-b033-61f034f4ad2f.png',
     'agent-profile/origin/3d0110ea-94a1-4141-8c57-e35a8d21ec9d.png'
         ])[n.num],
    '평일 09:00 ~ 18:00',
    '내비자 법률 사무소 ' || n.num || '호',
    '서울특별시 강남구 테헤란로 ' || (100 + n.num),
    '내비자 빌딩 ' || n.num || '층',
    '전직 출입국 관리소 공무원 출신',
    '010-1234-' || LPAD(n.num::text, 4, '0'),
    'LIC-' || (1000 + n.num),
    '2020-01-01'::DATE,
    'P-' || n.num,
    'MGT-' || n.num,
    '외국인 비자 업무 전문 행정사입니다.',
    (100.0 - (n.num * 2)), -- 활동 점수 차등 부여
    NOW(),
    NOW()
FROM (
    SELECT u.user_id, row_number() OVER () as num
    FROM users u
    WHERE u.user_type = 'VALID_AGENT' AND u.email LIKE 'agent_valid_%'
    ) n
    JOIN users u ON u.user_id = n.user_id;

-- 9. 행정사 언어(Languages) 매핑
INSERT INTO agent_language (agent_id, language_id, created_at, updated_at)
SELECT
    ap.agent_id,
    al.language_id,
    NOW(),
    NOW()
FROM agent_profile ap
         JOIN users u ON ap.user_id = u.user_id
         CROSS JOIN LATERAL (
    SELECT language_id FROM language
    WHERE ap.agent_id IS NOT NULL
    ORDER BY random() LIMIT 1
) al
WHERE u.email LIKE 'agent_valid_%';

-- 10. 행정사 전문분야(Specialized Jobs) 매핑
INSERT INTO agent_specialized_job (agent_id, job_code_id, created_at, updated_at)
SELECT
    ap.agent_id,
    aj.job_code_id,
    NOW(),
    NOW()
FROM agent_profile ap
         JOIN users u ON ap.user_id = u.user_id
         CROSS JOIN LATERAL (
    SELECT job_code_id FROM job_code
    WHERE ap.agent_id IS NOT NULL
    ORDER BY random() LIMIT 2
) aj
WHERE u.email LIKE 'agent_valid_%';

-- 11. FILLED_FOREIGNER 유저(20명)에 대한 E7 비자 타겟 경력 사항(Careers) 생성
-- 실제 E7 전문인력(E-7-1) 및 숙련기능인력(E-7-3) 직종을 고려한 자연어 데이터
INSERT INTO foreigner_careers (
    foreigner_id,
    company_name,
    job_title,
    start_date,
    end_date,
    is_work,
    created_at,
    updated_at
)
SELECT
    p.foreigner_id,
    'Global Tech Solutions ' || n.num,
    (ARRAY[
         '컴퓨터 시스템 설계 및 분석가', '응용 소프트웨어 개발자', '데이터베이스 관리자', '정보보안 전문가',
     '기계공학 기술자', '자동차 설계 엔지니어', '반도체 공정 기술자', '전자 회로 설계자',
     '해외 영업 마케터', '글로벌 비즈니스 컨설턴트', '경영지원 전문가', '재무 회계 분석가',
     '호텔 리셉션 매니저', '관광 숙박 시설 총지배인', 'MICE 컨벤션 기획사', '관광 통역 가이드',
     '조선 용접 기술자', '선박 도장공', '기계 장비 수리 전문가', '특수 용접 마스터'
         ])[n.num],
    '2023-01-01'::DATE,
    '2024-12-31'::DATE,
    false,
    NOW(),
    NOW()
FROM (
    SELECT p.foreigner_id, row_number() OVER () as num
    FROM foreigner_profile p
    JOIN users u ON p.user_id = u.user_id
    WHERE u.email LIKE 'foreigner_filled_%'
    ) n
    JOIN foreigner_profile p ON p.foreigner_id = n.foreigner_id;

-- 12. 경력 기반 유사도(Similarity) 매핑
INSERT INTO foreigner_similarity (foreigner_id, similarity_list, job_code_id_list, created_at, updated_at)
SELECT
    p.foreigner_id,
    -- 1. 유사도 점수 배열 (JSON 기반)
    CASE c.job_title
        WHEN '컴퓨터 시스템 설계 및 분석가' THEN ARRAY[0.8695249663370375, 0.684920098870294, 0.6794484879000466]
        WHEN '응용 소프트웨어 개발자' THEN ARRAY[0.8678790194593905, 0.7800014216118683, 0.7213030945109602]
        WHEN '데이터베이스 관리자' THEN ARRAY[0.704608427551752, 0.6923325857587237, 0.6859547066536098]
        WHEN '정보보안 전문가' THEN ARRAY[0.8378449346564267, 0.7055369440324527, 0.7015231383870628]
        WHEN '기계공학 기술자' THEN ARRAY[0.8408110801038443, 0.7510350003413269, 0.747971968083003]
        WHEN '자동차 설계 엔지니어' THEN ARRAY[0.7153968778269383, 0.6923833326440338, 0.6594977496785934]
        WHEN '반도체 공정 기술자' THEN ARRAY[0.7562130906569406, 0.7293866041732799, 0.7255579154480888]
        WHEN '전자 회로 설계자' THEN ARRAY[0.7104009966211045, 0.6903846796232596, 0.6897866505877147]
        WHEN '해외 영업 마케터' THEN ARRAY[0.7522807719228475, 0.7029859092605403, 0.7005740935252751]
        WHEN '글로벌 비즈니스 컨설턴트' THEN ARRAY[0.6584834270296407, 0.6572101139950811, 0.652161955842808]
        WHEN '경영지원 전문가' THEN ARRAY[0.8062064768893653, 0.7873543435092678, 0.7449821018691232]
        WHEN '재무 회계 분석가' THEN ARRAY[0.6917780152195694, 0.6651360691194087, 0.661054698119538]
        WHEN '호텔 리셉션 매니저' THEN ARRAY[0.7243864781268937, 0.6751228480176716, 0.6681956935322471]
        WHEN '관광 숙박 시설 총지배인' THEN ARRAY[0.701493783865055, 0.6944092518892772, 0.6610796569868513]
        WHEN 'MICE 컨벤션 기획사' THEN ARRAY[0.7158716909022538, 0.6379076224832472, 0.632821690320774]
        WHEN '관광 통역 가이드' THEN ARRAY[0.8018374423793353, 0.6821517765660676, 0.6522383316948954]
        WHEN '조선 용접 기술자' THEN ARRAY[0.8161274845315725, 0.7032696365796048, 0.698635086565981]
        WHEN '선박 도장공' THEN ARRAY[0.8627554160739598, 0.7097674179381581, 0.6983385100972554]
        WHEN '기계 장비 수리 전문가' THEN ARRAY[0.7193838758528607, 0.6827957042541763, 0.6778729620155841]
        WHEN '특수 용접 마스터' THEN ARRAY[0.7222412211218481, 0.6833994218615422, 0.6617971558509027]
        END,
    -- 2. 해당 이름에 맞는 job_code_id 리스트 배열
    (SELECT ARRAY_AGG(jc.job_code_id ORDER BY t.ord) FROM (
                                                              SELECT unnest_val as name, row_number() OVER() as ord
                                                              FROM unnest(CASE c.job_title
                                                                              WHEN '컴퓨터 시스템 설계 및 분석가' THEN ARRAY['컴퓨터시스템 설계 및 분석가', '네트워크시스템 개발자', '시스템 소프트웨어 개발자']
                                                                              WHEN '응용 소프트웨어 개발자' THEN ARRAY['응용 소프트웨어 개발자', '시스템 소프트웨어 개발자', '웹 개발자']
                                                                              WHEN '데이터베이스 관리자' THEN ARRAY['데이터 전문가', '정보통신관련 관리자', '기타 전문서비스 관리자']
                                                                              WHEN '정보보안 전문가' THEN ARRAY['정보 보안 전문가', '법률 전문가', '데이터 전문가']
                                                                              WHEN '기계공학 기술자' THEN ARRAY['기계공학 기술자', '전기공학 기술자', '화학공학 기술자']
                                                                              WHEN '자동차 설계 엔지니어' THEN ARRAY['자동차·조선·비행기·철도차량공학 전문가', '디자이너', '기계공학 기술자']
                                                                              WHEN '반도체 공정 기술자' THEN ARRAY['화학공학 기술자', '전자공학 기술자', '전기공학 기술자']
                                                                              WHEN '전자 회로 설계자' THEN ARRAY['전자공학 기술자', '디자이너', '전기공학 기술자']
                                                                              WHEN '해외 영업 마케터' THEN ARRAY['해외 영업원', '기술 영업원', '영업 및 판매 관련 관리자']
                                                                              WHEN '글로벌 비즈니스 컨설턴트' THEN ARRAY['경영 및 진단 전문가', '교육관련 전문가', '상품기획 전문가']
                                                                              WHEN '경영지원 전문가' THEN ARRAY['경영지원 관리자', '경영 및 진단 전문가', '기술경영 전문가']
                                                                              WHEN '재무 회계 분석가' THEN ARRAY['금융 및 보험 전문가', '컴퓨터시스템 설계 및 분석가', '경영 및 진단 전문가']
                                                                              WHEN '호텔 리셉션 매니저' THEN ARRAY['호텔 접수 사무원', '경영지원 관리자', '숙박·여행·오락 및 스포츠 관련 관리자']
                                                                              WHEN '관광 숙박 시설 총지배인' THEN ARRAY['음식서비스관련 관리자', '숙박·여행·오락 및 스포츠 관련 관리자', '문화·예술·디자인 및 영상관련 관리자']
                                                                              WHEN 'MICE 컨벤션 기획사' THEN ARRAY['행사 기획자', '상품기획 전문가', '여행상품 개발자']
                                                                              WHEN '관광 통역 가이드' THEN ARRAY['관광 통역 안내원', '번역가·통역가', '여행상품 개발자']
                                                                              WHEN '조선 용접 기술자' THEN ARRAY['조선용접공', '선박 도장공', '일반 제조업체 및 건설업체 숙련기능공']
                                                                              WHEN '선박 도장공' THEN ARRAY['선박 도장공', '조선용접공', '선박 전기원']
                                                                              WHEN '기계 장비 수리 전문가' THEN ARRAY['기계공학 기술자', '항공기 정비원', '플랜트공학 기술자']
                                                                              WHEN '특수 용접 마스터' THEN ARRAY['조선용접공', '일반 제조업체 및 건설업체 숙련기능공', '해외기술전문학교 기술강사']
                                                                  END) AS unnest_val
                                                          ) t
                                                              JOIN job_code jc ON jc.name = t.name
    ),
    NOW(),
    NOW()
FROM foreigner_profile p
         JOIN foreigner_careers c ON p.foreigner_id = c.foreigner_id
WHERE p.user_id IN (SELECT user_id FROM users WHERE email LIKE 'foreigner_filled_%');

-- 13. FILLED_FOREIGNER 유저(20명)에 대한 희망 직무(Expected Company) 생성
INSERT INTO foreigner_expected_company (foreigner_id, company_name, job_title, start_date, created_at, updated_at)
SELECT
    p.foreigner_id,
    'Future Labs Korea ' || n.num,
    (ARRAY[
         'Full-stack 서버 개발자', 'AI 딥러닝 알고리즘 엔지니어', '클라우드 보안 아키텍트', '빅데이터 사이언티스트',
     '자율주행 기계 설계 연구원', 'EV 배터리 공정 엔지니어', '차세대 반도체 장비 제어', '스마트 디바이스 하드웨어 개발',
     '글로벌 디지털 마케팅 전문가', '전략 경영 데이터 분석가', 'ERP 인사 시스템 관리자', '국제 금융 자산 운용가',
     '5성급 호텔 프런트 오피스 팀장', '글로벌 전시 기획 매니저', '외국인 전용 카지노 딜러', '여행 상품 기획 마스터',
     '고부가가치 친환경 선박 용접사', '조선소 스마트 도장 감독', '정밀 기계 부품 가공 전문가', '플랜트 건설 용접 마스터'
         ])[n.num],
    '2026-03-01'::DATE,
    NOW(),
    NOW()
FROM (
    SELECT p.foreigner_id, row_number() OVER () as num
    FROM foreigner_profile p
    JOIN users u ON p.user_id = u.user_id
    WHERE u.email LIKE 'foreigner_filled_%'
    ) n
    JOIN foreigner_profile p ON p.foreigner_id = n.foreigner_id;

-- 14. FILLED_FOREIGNER 유저(20명)에 대한 학력 정보(Education) 생성
INSERT INTO foreigner_education (foreigner_id, degree_level, school_name, major_name, created_at, updated_at)
SELECT
    p.foreigner_id,
    (CASE
         WHEN n.num % 3 = 0 THEN 'ABOVE_MASTER'
         WHEN n.num % 3 = 1 THEN 'BACHELOR'
         ELSE 'BACHELOR'
        END)::varchar,
    'Global University of ' || (ARRAY['Seoul', 'Technology', 'Engineering', 'Management', 'Service'])[floor(random() * 5 + 1)],
    (CASE
        WHEN n.num <= 4 THEN 'Computer Science & Engineering'
        WHEN n.num <= 8 THEN 'Mechanical & Electronic Engineering'
        WHEN n.num <= 12 THEN 'Business Administration'
        WHEN n.num <= 16 THEN 'Hotel & Tourism Management'
        ELSE 'Industrial Heavy Skills'
    END),
    NOW(),
    NOW()
FROM (
    SELECT p.foreigner_id, row_number() OVER () as num
    FROM foreigner_profile p
    JOIN users u ON p.user_id = u.user_id
    WHERE u.email LIKE 'foreigner_filled_%'
    ) n
    JOIN foreigner_profile p ON p.foreigner_id = n.foreigner_id;