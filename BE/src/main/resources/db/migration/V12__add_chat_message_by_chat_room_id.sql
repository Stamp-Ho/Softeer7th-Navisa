INSERT INTO chat_message (
    created_at,
    updated_at,
    sent_at,
    content,
    is_read_by_other,
    message_type,
    sender_id,
    chat_room_id
)
SELECT
    cr.last_chatted_at,        -- createdAt을 lastChattedAt과 동일하게
    cr.last_chatted_at,        -- updatedAt을 lastChattedAt과 동일하게
    cr.last_chatted_at,        -- sentAt을 lastChattedAt과 동일하게
    '채팅방이 생성되었습니다.',       -- 기본 메시지 내용 (원하는 문구로 수정 가능)
    false,                      -- 읽음 처리 여부
    'TEXT',                    -- 메시지 타입 (TEXT 등 정의된 값 사용)
    cr.agent_id,               -- 발신자 ID (임시로 agent_id 사용, 필요시 수정)
    cr.chat_room_id            -- 해당 채팅방 ID
FROM chat_room cr
         LEFT JOIN chat_message cm ON cr.chat_room_id = cm.chat_room_id
WHERE cm.chat_message_id IS NULL;