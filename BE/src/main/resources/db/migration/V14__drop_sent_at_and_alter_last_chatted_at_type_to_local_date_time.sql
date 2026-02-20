-- 1. chat_message 테이블에서 sent_at 컬럼 삭제
ALTER TABLE chat_message DROP COLUMN sent_at;

-- 2. chat_room 테이블의 last_chatted_at 타입 변경 (데이터 유지)
-- 'timestamp(6) without time zone'이 Java의 LocalDateTime과 매핑되는 타입입니다.
ALTER TABLE chat_room
ALTER COLUMN last_chatted_at TYPE timestamp(6) WITHOUT TIME ZONE
    USING last_chatted_at AT TIME ZONE 'UTC',
ALTER COLUMN last_chatted_at DROP NOT NULL; -- NULL 허용 제약 조건 추가