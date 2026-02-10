-- 1. 신규 컬럼 추가
ALTER TABLE public.visa_application_form ADD COLUMN IF NOT EXISTS profile_object_key VARCHAR(255);

-- 2. 데이터 이관 (ForeignerProfile -> VisaApplicationForm)
UPDATE public.visa_application_form vaf
SET profile_object_key = fp.profile_object_key
    FROM public.foreigner_profile fp
WHERE vaf.foreigner_id = fp.foreigner_id
  AND fp.profile_object_key IS NOT NULL;

-- 3. 기존 foreigner_profile 테이블에서 이전 컬럼 삭제
ALTER TABLE public.foreigner_profile DROP COLUMN IF EXISTS profile_object_key;