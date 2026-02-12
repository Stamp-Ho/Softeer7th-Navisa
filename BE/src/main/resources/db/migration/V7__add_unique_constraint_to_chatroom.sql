ALTER TABLE chat_room ADD CONSTRAINT uk_agent_foreigner UNIQUE (agent_id, foreigner_id);
