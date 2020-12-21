ALTER TABLE {users-table} ADD COLUMN current_server DATA_TYPE_VARCHAR DEFAULT NULL;

ALTER TABLE {bans-table} ADD COLUMN punishment_uid DATA_TYPE_VARCHAR;
ALTER TABLE {mutes-table} ADD COLUMN punishment_uid DATA_TYPE_VARCHAR;

CREATE INDEX idx_bans_puid ON {bans-table} (punishment_uid);
CREATE INDEX idx_mutes_puid ON {mutes-table} (punishment_uid);