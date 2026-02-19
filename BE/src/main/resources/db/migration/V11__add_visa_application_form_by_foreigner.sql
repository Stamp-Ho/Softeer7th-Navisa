INSERT INTO visa_application_form (
    application_form_id,
    created_at,
    updated_at,
    current_step,
    is_done,
    is_finished,
    foreigner_id,
    job_code_id,
    agent_id
)
SELECT
    gen_random_uuid(),    -- 새로운 UUID 생성
    now(),                -- 현재 시간
    now(),                -- 현재 시간
    1,                    -- 초기 단계 설정 (예: 1)
    false,                -- is_done 기본값
    false,                -- is_finished 기본값
    fp.foreigner_id,      -- foreigner_profile의 ID
    NULL,              -- job_code_id는 null로 설정
    NULL                  -- agent_id는 null로 설정
FROM foreigner_profile fp
         LEFT JOIN visa_application_form vaf ON fp.foreigner_id = vaf.foreigner_id
WHERE vaf.foreigner_id IS NULL; -- 기존에 신청서가 없는 외국인만 필터링
