CREATE TABLE transmissions
(
    submission_id       uuid REFERENCES submissions (id) PRIMARY KEY,
    created_at          TIMESTAMP WITH TIME ZONE,
    updated_at          TIMESTAMP WITH TIME ZONE,
    sent_at             TIMESTAMP WITH TIME ZONE,
    status              varchar          DEFAULT 'Queued',
    errors              JSONB
);
