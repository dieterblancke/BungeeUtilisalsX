CREATE TABLE IF NOT EXISTS "{users-table}"
(
  "id"         SERIAL PRIMARY KEY NOT NULL,
  "uuid"       VARCHAR(36) UNIQUE NOT NULL,
  "username"   VARCHAR(32)        NOT NULL,
  "ip"         VARCHAR(32)        NOT NULL,
  "language"   VARCHAR(24)        NOT NULL,
  "firstlogin" TIMESTAMP          NOT NULL,
  "lastlogout" TIMESTAMP          NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users
  ON "{users-table}" ("uuid", "username", "ip");


CREATE TABLE IF NOT EXISTS "{friends-table}"
(
  "userid"      INTEGER REFERENCES "users-table}" (id)  NOT NULL,
  "friendid"    INTEGER REFERENCES "{users-table}" (id) NOT NULL,
  "friendsince" TIMESTAMP                               NOT NULL,
  PRIMARY KEY (userid, friendid)
);

CREATE INDEX IF NOT EXISTS idx_friends
  ON "{friends-table}" ("userid", "friendid");


CREATE TABLE IF NOT EXISTS "{friendrequests-table}"
(
  "userid"      INTEGER REFERENCES "{users-table}" (id) NOT NULL,
  "friendid"    INTEGER REFERENCES "{users-table}" (id) NOT NULL,
  "friendsince" TIMESTAMP                               NOT NULL,
  PRIMARY KEY (userid, friendid)
);

CREATE INDEX IF NOT EXISTS idx_friendreq
  ON "{friendrequests-table}" ("userid", "friendid");


CREATE TABLE IF NOT EXISTS "{friendsettings-table}"
(
  userid   INTEGER REFERENCES "{friendsettings-table}" (id) NOT NULL,
  settings JSON                                             NOT NULL,
  PRIMARY KEY (userid)
);

CREATE INDEX IF NOT EXISTS idx_friendreq
  ON "{friendsettings-table}" ("userid", "friendid");


CREATE TABLE IF NOT EXISTS `{bans-table}`
(
  id          SERIAL PRIMARY KEY                            NOT NULL,
  uuid        VARCHAR(48) REFERENCES "{users-table}" (uuid) NOT NULL,
  user        VARCHAR(32)                                   NOT NULL,
  ip          VARCHAR(32)                                   NOT NULL,
  reason      TEXT                                          NOT NULL,
  server      VARCHAR(32)                                   NOT NULL,
  date        TIMESTAMP                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active      BOOLEAN                                       NOT NULL,
  executed_by VARCHAR(64)                                   NOT NULL,
  duration    BIGINT                                        NOT NULL,
  type        VARCHAR(16)                                   NOT NULL,
  removed     BOOLEAN                                       NOT NULL DEFAULT 0,
  removed_by  VARCHAR(32)
);

CREATE INDEX IF NOT EXISTS idx_bans
  ON "{bans-table}" ("id", "uuid", "user", "ip", "active", "server");


CREATE TABLE IF NOT EXISTS `{mutes-table}`
(
  id          SERIAL PRIMARY KEY                            NOT NULL,
  uuid        VARCHAR(48) REFERENCES "{users-table}" (uuid) NOT NULL,
  user        VARCHAR(32)                                   NOT NULL,
  ip          VARCHAR(32)                                   NOT NULL,
  reason      TEXT                                          NOT NULL,
  server      VARCHAR(32)                                   NOT NULL,
  date        TIMESTAMP                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active      BOOLEAN                                       NOT NULL,
  executed_by VARCHAR(64)                                   NOT NULL,
  duration    BIGINT                                        NOT NULL,
  type        VARCHAR(16)                                   NOT NULL,
  removed     BOOLEAN                                       NOT NULL DEFAULT 0,
  removed_by  VARCHAR(32)
);

CREATE INDEX IF NOT EXISTS idx_mutes
  ON "{mutes-table}" ("id", "uuid", "user", "ip", "active", "server");


CREATE TABLE IF NOT EXISTS "{kicks-table}"
(
  "id"          SERIAL PRIMARY KEY                            NOT NULL,
  "uuid"        VARCHAR(36) REFERENCES "{users-table}" (uuid) NOT NULL,
  "user"        VARCHAR(32)                                   NOT NULL,
  "ip"          VARCHAR(32)                                   NOT NULL,
  "reason"      TEXT                                          NOT NULL,
  "server"      VARCHAR(32)                                   NOT NULL,
  "date"        TIMESTAMP DEFAULT CURRENT_TIMESTAMP           NOT NULL,
  "executed_by" VARCHAR(64)                                   NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_kicks
  ON "{kicks-table}" ("id", "uuid", "user", "ip");


CREATE TABLE IF NOT EXISTS "{warns-table}"
(
  "id"          SERIAL PRIMARY KEY                            NOT NULL,
  "uuid"        VARCHAR(36) REFERENCES "{users-table}" (uuid) NOT NULL,
  "user"        VARCHAR(32)                                   NOT NULL,
  "ip"          VARCHAR(32)                                   NOT NULL,
  "reason"      TEXT                                          NOT NULL,
  "server"      VARCHAR(32)                                   NOT NULL,
  "date"        TIMESTAMP DEFAULT CURRENT_TIMESTAMP           NOT NULL,
  "executed_by" VARCHAR(64)                                   NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_warns
  ON "{warns-table}" ("id", "uuid", "user", "ip");