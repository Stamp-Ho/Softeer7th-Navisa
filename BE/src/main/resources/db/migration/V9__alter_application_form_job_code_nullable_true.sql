-- job_code_id 컬럼의 NOT NULL 제약 조건 제거
ALTER TABLE visa_application_form
    ALTER COLUMN job_code_id DROP NOT NULL;