ALTER TABLE bu_users ADD COLUMN current_server DATA_TYPE_VARCHAR DEFAULT NULL;

ALTER TABLE bu_bans ADD COLUMN punishment_uid DATA_TYPE_VARCHAR;
ALTER TABLE bu_mutes ADD COLUMN punishment_uid DATA_TYPE_VARCHAR;

CREATE INDEX idx_bans_puid ON bu_bans (punishment_uid);
CREATE INDEX idx_mutes_puid ON bu_mutes (punishment_uid);