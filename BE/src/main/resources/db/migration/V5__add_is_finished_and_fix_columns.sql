ALTER TABLE public.visa_application_form ADD COLUMN IF NOT EXISTS is_finished BOOLEAN DEFAULT FALSE NOT NULL;

ALTER TABLE public.visa_application_form ALTER COLUMN agent_id DROP NOT NULL;