CREATE TABLE IF NOT EXISTS "{users-table}" (
  "id"         SERIAL PRIMARY KEY      NOT NULL,
  "uuid"       VARCHAR(36) UNIQUE      NOT NULL,
  "username"   VARCHAR(32) UNIQUE      NOT NULL,
  "ip"         VARCHAR(32)             NOT NULL,
  "language"   VARCHAR(24)             NOT NULL
);

µCREATE INDEX IF NOT EXISTS idx_users_id ON {users-table} (uuid);
CREATE INDEX IF NOT EXISTS idx_users_uuid ON {users-table} (username);
CREATE INDEX IF NOT EXISTS idx_users_ip ON {users-table} (ip);



CREATE TABLE IF NOT EXISTS "{friends-table}" (
  "userid"      INTEGER REFERENCES {users-table}(id)    NOT NULL,
  "friendid"    INTEGER REFERENCES {users-table}(id)    NOT NULL,
  "friendsince" TIMESTAMP                               NOT NULL,
  PRIMARY KEY (userid, friendid)
);

CREATE INDEX idx_friends_id ON {friends-table} (userid);
CREATE INDEX idx_friends_fid ON {friends-table} (friendid);



CREATE TABLE IF NOT EXISTS "{friendrequests-table}" (
  "userid"      INTEGER REFERENCES {users-table}(id)    NOT NULL,
  "friendid"    INTEGER REFERENCES {users-table}(id)    NOT NULL,
  "friendsince" TIMESTAMP                               NOT NULL,
  PRIMARY KEY (userid, friendid)
);

CREATE INDEX idx_friendreq_id ON {friendrequests-table} (userid);
CREATE INDEX idx_friendreq_fid ON {friendrequests-table} (friendid);