--- i번째 VALID_AGENT와 i번째 FILLED_FOREIGNER 사이에 채팅방 생성 ---

INSERT INTO chat_room (agent_id, foreigner_id, status, last_chatted_at, created_at, updated_at)
SELECT
    ap.agent_id,
    fp.foreigner_id,
    'DEFAULT',
    NOW(), NOW(), NOW()
FROM (
         SELECT ap.agent_id, ROW_NUMBER() OVER (ORDER BY ap.agent_id) as num
         FROM agent_profile ap JOIN users u ON ap.user_id = u.user_id
         WHERE u.email LIKE 'agent_valid_%'
     ) ap
         JOIN (
    SELECT fp.foreigner_id, ROW_NUMBER() OVER (ORDER BY fp.foreigner_id) as num
    FROM foreigner_profile fp JOIN users u ON fp.user_id = u.user_id
    WHERE u.email LIKE 'foreigner_filled_%'
) fp ON ap.num = fp.num;

--- i번째 VALID_AGENT와 i번째 FILLED_FOREIGNER 사이에 제안 생성
INSERT INTO proposal (chat_room_id, status, sender_id, created_at, updated_at)
SELECT
    cr.chat_room_id,
    'COMPLETED', -- 수락된 제안이어야 리뷰 가능
    cr.agent_id,
    NOW(), NOW()
FROM chat_room cr;

-- 리뷰 삽입
INSERT INTO agent_review (
    agent_profile_id,
    foreigner_profile_id,
    proposal_id,
    feedback_content,
    similarity_list,
    created_at,
    updated_at
)
SELECT
    cr.agent_id,
    cr.foreigner_id,
    p.proposal_id,
    CASE
        WHEN mod(p.proposal_id, 3) = 0 THEN '매우 만족스러운 상담이었습니다!'
        WHEN mod(p.proposal_id, 3) = 1 THEN '답변이 빠르고 친절해요.'
        ELSE '비자 발급 과정이 훨씬 수월해졌습니다.'
        END,
    ARRAY[round(random()::numeric, 2), round(random()::numeric, 2), round(random()::numeric, 2)],
    NOW(), NOW()
FROM proposal p
    JOIN chat_room cr ON p.chat_room_id = cr.chat_room_id;

-- 리뷰 뱃지 삽입 ---
INSERT INTO agent_badge (badge_id, agent_review_id, created_at, updated_at)
SELECT
    b.badge_id,
    ar.agent_review_id,
    NOW(),
    NOW()
FROM agent_review ar
         CROSS JOIN LATERAL (
    SELECT badge_id
    FROM badge
    WHERE badge_id BETWEEN 1 AND 15
      -- 중요: 외부 쿼리의 ar.agent_review_id를 사용하여 캐싱 방지
      AND ar.agent_review_id IS NOT NULL
    ORDER BY (random() + (ar.agent_review_id * 0)) -- 각 행마다 고유한 시드 유도
        LIMIT floor(random() * 3 + 1)
) b;

-- 리뷰 뱃지 summary 삽입 ---
INSERT INTO agent_badge_summary (agent_id, badge_id, count, created_at, updated_at)
SELECT
    ar.agent_profile_id as agent_id,
    ab.badge_id,
    COUNT(*) as count, -- 해당 행정사가 해당 뱃지를 받은 총 횟수
    NOW(),
    NOW()
FROM agent_badge ab
    JOIN agent_review ar ON ab.agent_review_id = ar.agent_review_id
GROUP BY ar.agent_profile_id, ab.badge_id
ON CONFLICT (agent_id, badge_id)
    DO UPDATE SET
           count = EXCLUDED.count, -- 상세 내역을 기반으로 집계했으므로 덮어쓰기
           updated_at = NOW();

-- 행정사 특화 직무 summary 삽입 ---

INSERT INTO agent_specialized_job_summary (
    agent_id,
    job_code_id,
    count,
    accumulated_review_reliability,
    created_at,
    updated_at
)
WITH
-- 1단계: 외국인별 관심 직무 데이터를 개별 행으로 분리(Unnest)하고 상대적 가중치 계산
Step1_ForeignerJobScore AS (
    SELECT
        fs.foreigner_id,
        -- 배열 형태의 직무 ID 리스트를 행(Row)으로 변환
        unnest(fs.job_code_id_list) AS job_id,
        -- 신뢰도 가중치 계산 수식: (해당 직무 유사도 / 전체 직무 유사도의 합)
        -- 결과적으로 외국인이 가진 모든 관심 직무의 score_weight 합은 1.0이 됨
        unnest(fs.similarity_list) /
        NULLIF((SELECT sum(s) FROM unnest(fs.similarity_list) s), 0) AS score_weight
    FROM foreigner_similarity fs
),

-- 2단계: 실제로 작성된 리뷰(AgentReview)와 결합하여 행정사별/직무별 통계 집계
Step2_AgentTotalScore AS (
    SELECT
        ar.agent_profile_id AS agent_id,
        f.job_id AS job_code_id,
        -- 해당 행정사가 이 특정 직무와 연관된 리뷰를 받은 총 횟수
        COUNT(*) AS total_count,
        -- 외국인의 해당 직무 신뢰도 비중을 모두 합산하여 행정사의 누적 신뢰도로 변환
        SUM(f.score_weight) AS total_score
    FROM agent_review ar
             -- 리뷰를 쓴 외국인의 ID를 기준으로 위에서 만든 점수표와 매칭
             JOIN Step1_ForeignerJobScore f ON ar.foreigner_profile_id = f.foreigner_id
    GROUP BY ar.agent_profile_id, f.job_id
)

-- 3단계: 최종 집계 데이터를 테이블에 삽입하며, 중복 시 기존 데이터에 합산(Upsert)
SELECT
    agent_id,
    job_code_id,
    total_count,
    total_score,
    NOW(),
    NOW()
FROM Step2_AgentTotalScore
-- (행정사ID, 직무코드ID) 조합이 이미 존재할 경우 아래 업데이트 구문 실행
    ON CONFLICT (agent_id, job_code_id)
DO UPDATE SET
           -- 기존 누적 건수에 새로 집계된 건수를 더함
           count = agent_specialized_job_summary.count + EXCLUDED.count,
           -- 기존 누적 신뢰도 점수에 새로 계산된 신뢰도 점수를 더함
           accumulated_review_reliability = agent_specialized_job_summary.accumulated_review_reliability + EXCLUDED.accumulated_review_reliability,
           updated_at = NOW();


-- 잘못된 뱃지 상수 수정
UPDATE badge SET badge_name = 'KIND_CONSULTATION' WHERE badge_id = 13;