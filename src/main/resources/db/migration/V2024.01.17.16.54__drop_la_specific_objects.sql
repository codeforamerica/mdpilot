ALTER TABLE submissions DROP COLUMN rpa_status;
ALTER TABLE user_files DROP COLUMN rpa_status;
ALTER TABLE user_files DROP COLUMN rpa_status_description;
ALTER TABLE user_files DROP COLUMN rpa_process_start_time;
ALTER TABLE user_files DROP COLUMN rpa_process_end_time;

DROP TABLE transmissions;

DROP TYPE rpa_status_type;
