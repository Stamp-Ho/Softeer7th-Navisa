/* 기존 데이터 삭제 */

-- 통계 및 요약 테이블 삭제
DELETE FROM agent_specialized_job_summary;
DELETE FROM agent_badge_summary;

--  상세 내역 테이블 삭제
DELETE FROM agent_badge;
DELETE FROM agent_review;

-- 관계 형성 테이블 삭제
DELETE FROM chat_message;
DELETE FROM proposal;
DELETE FROM chat_room;

/* 새로운 데이터 추가 */

--- 1. 채팅방 생성 (행정사 500명과 외국인 1:1 순환 매칭) ---
INSERT INTO chat_room (agent_id, foreigner_id, status, last_chatted_at, created_at, updated_at)
SELECT ap.agent_id, fp.foreigner_id, 'DEFAULT', NOW(), NOW(), NOW()
FROM (
         SELECT ap.agent_id, ROW_NUMBER() OVER (ORDER BY ap.agent_id) as num
         FROM agent_profile ap JOIN users u ON ap.user_id = u.user_id
         WHERE u.email LIKE 'agent_valid_%'
     ) ap
         JOIN (
    SELECT fp.foreigner_id, ROW_NUMBER() OVER (ORDER BY fp.foreigner_id) as num
    FROM foreigner_profile fp JOIN users u ON fp.user_id = u.user_id
    WHERE u.email LIKE 'foreigner_filled_%'
) fp ON fp.num = (MOD(ap.num - 1, (SELECT COUNT(*) FROM foreigner_profile fp JOIN users u ON fp.user_id = u.user_id WHERE u.email LIKE 'foreigner_filled_%')) + 1);

--- 1.1 최초 채팅 메시지 생성 ---
INSERT INTO chat_message (created_at, updated_at, content, is_read_by_other, message_type, sender_id, chat_room_id)
SELECT cr.last_chatted_at, cr.last_chatted_at, '채팅방이 생성되었습니다.', false, 'TEXT', cr.agent_id, cr.chat_room_id
FROM chat_room cr
         LEFT JOIN chat_message cm ON cr.chat_room_id = cm.chat_room_id
WHERE cm.chat_message_id IS NULL;

--- 2. 상담 완료된 제안 생성 (리뷰 작성을 위한 필수 조건) ---
INSERT INTO proposal (chat_room_id, status, sender_id, created_at, updated_at)
SELECT cr.chat_room_id, 'COMPLETED', cr.agent_id, NOW(), NOW()
FROM chat_room cr;

--- 3. 리뷰 생성 ---
INSERT INTO agent_review (agent_profile_id, foreigner_profile_id, proposal_id, feedback_content, similarity_list, created_at, updated_at)
SELECT cr.agent_id, cr.foreigner_id, p.proposal_id,
       CASE
           WHEN mod(p.proposal_id, 3) = 0 THEN '매우 만족스러운 상담이었습니다!'
           WHEN mod(p.proposal_id, 3) = 1 THEN '답변이 빠르고 친절해요.'
           ELSE '비자 발급 과정이 훨씬 수월해졌습니다.'
           END,
       ARRAY[round(random()::numeric, 2), round(random()::numeric, 2), round(random()::numeric, 2)],
       NOW(), NOW()
FROM proposal p
    JOIN chat_room cr ON p.chat_room_id = cr.chat_room_id;

--- 4. 뱃지 상세 내역 삽입 (모든 행정사당 반드시 서로 다른 2개의 뱃지 생성) ---
INSERT INTO agent_badge (badge_id, agent_review_id, created_at, updated_at)
SELECT v.badge_id, ar.agent_review_id, NOW(), NOW()
FROM agent_review ar
         CROSS JOIN LATERAL (
    -- badge_id 1~15 사이에서 리뷰 ID를 기준으로 규칙적으로 2개 추출
    SELECT (MOD(ar.agent_review_id::int, 15) + 1) AS badge_id
    UNION ALL
    SELECT (MOD(ar.agent_review_id::int + 5, 15) + 1) AS badge_id
        ) v;

--- 5. 뱃지 요약 삽입 (인당 레코드 2개 보장) ---
INSERT INTO agent_badge_summary (agent_id, badge_id, count, created_at, updated_at)
SELECT ar.agent_profile_id, ab.badge_id, COUNT(*), NOW(), NOW()
FROM agent_badge ab
         JOIN agent_review ar ON ab.agent_review_id = ar.agent_review_id
GROUP BY ar.agent_profile_id, ab.badge_id
    ON CONFLICT (agent_id, badge_id)
DO UPDATE SET count = EXCLUDED.count, updated_at = NOW();

--- 6. 행정사 특화 직무 요약 (행정사 실제 직무 2개에 리뷰 점수 배분) ---
INSERT INTO agent_specialized_job_summary (agent_id, job_code_id, count, accumulated_review_reliability, created_at, updated_at)
WITH TargetJobs AS (
    -- 행정사가 실제로 등록한 직무 중 상위 2개만 추출 (정합성 핵심)
    SELECT agent_id, job_code_id, ROW_NUMBER() OVER (PARTITION BY agent_id ORDER BY job_code_id) as rn
    FROM agent_specialized_job
),
     Step1_ForeignerScore AS (
         -- 리뷰를 남긴 외국인의 유사도 리스트 평균 점수 활용
         SELECT fs.foreigner_id,
                AVG(val) as avg_score
         FROM foreigner_similarity fs,
              unnest(fs.similarity_list) val
         GROUP BY fs.foreigner_id
     )
SELECT
    ar.agent_profile_id,
    tj.job_code_id,
    COUNT(ar.agent_review_id),
    SUM(COALESCE(fs.avg_score, 0.5)), -- 외국인 점수가 없으면 기본값 0.5
    NOW(),
    NOW()
FROM agent_review ar
         JOIN TargetJobs tj ON ar.agent_profile_id = tj.agent_id
         LEFT JOIN Step1_ForeignerScore fs ON ar.foreigner_profile_id = fs.foreigner_id
WHERE tj.rn <= 2 -- 모든 행정사에게 최대 2개의 레코드만 생성되도록 제한
GROUP BY ar.agent_profile_id, tj.job_code_id
    ON CONFLICT (agent_id, job_code_id)
DO UPDATE SET
           count = EXCLUDED.count,
           accumulated_review_reliability = EXCLUDED.accumulated_review_reliability,
           updated_at = NOW();