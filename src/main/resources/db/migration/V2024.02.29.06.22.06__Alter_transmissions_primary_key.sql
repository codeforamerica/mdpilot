ALTER TABLE transmissions
    DROP CONSTRAINT transmissions_pkey,
    ADD COLUMN id UUID PRIMARY KEY,
    ADD UNIQUE(submission_id),
    ALTER COLUMN status DROP DEFAULT;
