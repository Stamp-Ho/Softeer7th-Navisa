ALTER TABLE public.visa_application_form ADD COLUMN exported_at TIMESTAMP;
ALTER TABLE public.visa_application_form DROP COLUMN is_once_exported;