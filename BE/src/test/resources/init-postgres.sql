CREATE EXTENSION IF NOT EXISTS vector;

CREATE OR REPLACE FUNCTION array_overlap(bigint[], bigint[]) RETURNS boolean AS $$
    SELECT $1 && $2;
$$ LANGUAGE sql IMMUTABLE;