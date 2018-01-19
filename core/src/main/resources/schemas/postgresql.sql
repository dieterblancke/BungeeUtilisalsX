CREATE TABLE IF NOT EXISTS "{users-table}" (
  "id"         SERIAL PRIMARY KEY      NOT NULL,
  "uuid"       VARCHAR(36) UNIQUE      NOT NULL,
  "username"   VARCHAR(32) UNIQUE      NOT NULL,
  "ip"         VARCHAR(32)             NOT NULL,
  "language"   VARCHAR(24)             NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users ON {users-table} ("uuid", "username", "ip");



CREATE TABLE IF NOT EXISTS "{friends-table}" (
  "userid"      INTEGER REFERENCES {users-table} (id)    NOT NULL,
  "friendid"    INTEGER REFERENCES {users-table} (id)    NOT NULL,
  "friendsince" TIMESTAMP                                NOT NULL,
  PRIMARY KEY (userid, friendid)
);

CREATE INDEX IF NOT EXISTS idx_friends ON {friends-table} ("userid", "friendid");



CREATE TABLE IF NOT EXISTS "{friendrequests-table}" (
  "userid"      INTEGER REFERENCES {users-table} (id)    NOT NULL,
  "friendid"    INTEGER REFERENCES {users-table} (id)    NOT NULL,
  "friendsince" TIMESTAMP                                NOT NULL,
  PRIMARY KEY (userid, friendid)
);

CREATE INDEX IF NOT EXISTS idx_friendreq ON {friendrequests-table} ("userid", "friendid");



CREATE TABLE IF NOT EXISTS {bans-table} (
    "id"           SERIAL PRIMARY KEY                             NOT NULL,
    "uuid"         VARCHAR(36) REFERENCES {users-table} (uuid)    NOT NULL,
    "user"         VARCHAR(32)                                    NOT NULL,
    "ip"           VARCHAR(32)                                    NOT NULL,
    "reason"       TEXT                                           NOT NULL,
    "server"       VARCHAR(32)                                    NOT NULL,
    "date"         TIMESTAMP DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    "active"       BOOLEAN                                        NOT NULL,
    "executed_by"  VARCHAR(64)                                    NOT NULL,
    "removed_by"   VARCHAR(32)
);

CREATE INDEX IF NOT EXISTS idx_bans ON {bans-table} ("id", "uuid", "user", "ip", "active");



CREATE TABLE IF NOT EXISTS {ipbans-table} (
    "id"           SERIAL PRIMARY KEY                             NOT NULL,
    "uuid"         VARCHAR(36) REFERENCES {users-table} (uuid)    NOT NULL,
    "user"         VARCHAR(32)                                    NOT NULL,
    "ip"           VARCHAR(32)                                    NOT NULL,
    "reason"       TEXT                                           NOT NULL,
    "server"       VARCHAR(32)                                    NOT NULL,
    "date"         TIMESTAMP DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    "active"       BOOLEAN                                        NOT NULL,
    "executed_by"  VARCHAR(64)                                    NOT NULL,
    "removed_by"   VARCHAR(32)
);

CREATE INDEX IF NOT EXISTS idx_ipbans ON {ipbans-table} ("id", "uuid", "user", "ip", "active");



CREATE TABLE IF NOT EXISTS {tempbans-table} (
    "id"           SERIAL PRIMARY KEY                             NOT NULL,
    "uuid"         VARCHAR(36) REFERENCES {users-table} (uuid)    NOT NULL,
    "user"         VARCHAR(32)                                    NOT NULL,
    "ip"           VARCHAR(32)                                    NOT NULL,
    "time"         BIGINT                                         NOT NULL,
    "reason"       TEXT                                           NOT NULL,
    "server"       VARCHAR(32)                                    NOT NULL,
    "date"         TIMESTAMP DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    "active"       BOOLEAN                                        NOT NULL,
    "executed_by"  VARCHAR(64)                                    NOT NULL,
    "removed_by"   VARCHAR(32)
);

CREATE INDEX IF NOT EXISTS idx_tempbans ON {tempbans-table} ("id", "uuid", "user", "ip", "active");



CREATE TABLE IF NOT EXISTS {iptempbans-table} (
    "id"           SERIAL PRIMARY KEY                             NOT NULL,
    "uuid"         VARCHAR(36) REFERENCES {users-table} (uuid)    NOT NULL,
    "user"         VARCHAR(32)                                    NOT NULL,
    "ip"           VARCHAR(32)                                    NOT NULL,
    "time"         BIGINT                                         NOT NULL,
    "reason"       TEXT                                           NOT NULL,
    "server"       VARCHAR(32)                                    NOT NULL,
    "date"         TIMESTAMP DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    "active"       BOOLEAN                                        NOT NULL,
    "executed_by"  VARCHAR(64)                                    NOT NULL,
    "removed_by"   VARCHAR(32)
);

CREATE INDEX IF NOT EXISTS idx_iptempbans ON {iptempbans-table} ("id", "uuid", "user", "ip", "active");



CREATE TABLE IF NOT EXISTS {mutes-table} (
    "id"            SERIAL PRIMARY KEY                             NOT NULL,
    "uuid"          VARCHAR(36) REFERENCES {users-table} (uuid)    NOT NULL,
    "user"          VARCHAR(32)                                    NOT NULL,
    "ip"            VARCHAR(32)                                    NOT NULL,
    "reason"        TEXT                                           NOT NULL,
    "server"        VARCHAR(32)                                    NOT NULL,
    "date"          TIMESTAMP DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    "active"        BOOLEAN                                        NOT NULL,
    "executed_by"   VARCHAR(64)                                    NOT NULL,
    "removed_by"    VARCHAR(32)
);

CREATE INDEX IF NOT EXISTS idx_mutes ON {mutes-table} ("id", "uuid", "user", "ip", "active");



CREATE TABLE IF NOT EXISTS {ipmutes-table} (
    "id"            SERIAL PRIMARY KEY                             NOT NULL,
    "uuid"          VARCHAR(36) REFERENCES {users-table} (uuid)    NOT NULL,
    "user"          VARCHAR(32)                                    NOT NULL,
    "ip"            VARCHAR(32)                                    NOT NULL,
    "reason"        TEXT                                           NOT NULL,
    "server"        VARCHAR(32)                                    NOT NULL,
    "date"          TIMESTAMP DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    "active"        BOOLEAN                                        NOT NULL,
    "executed_by"   VARCHAR(64)                                    NOT NULL,
    "removed_by"    VARCHAR(32)
);

CREATE INDEX IF NOT EXISTS idx_ipmutes ON {ipmutes-table} ("id", "uuid", "user", "ip", "active");



CREATE TABLE IF NOT EXISTS {tempmutes-table} (
    "id"            SERIAL PRIMARY KEY                             NOT NULL,
    "uuid"          VARCHAR(36) REFERENCES {users-table} (uuid)    NOT NULL,
    "user"          VARCHAR(32)                                    NOT NULL,
    "ip"            VARCHAR(32)                                    NOT NULL,
    "time"          BIGINT                                         NOT NULL,
    "reason"        TEXT                                           NOT NULL,
    "server"        VARCHAR(32)                                    NOT NULL,
    "date"          TIMESTAMP DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    "active"        BOOLEAN                                        NOT NULL,
    "executed_by"   VARCHAR(64)                                    NOT NULL,
    "removed_by"    VARCHAR(32)
);

CREATE INDEX IF NOT EXISTS idx_tempmutes ON {tempmutes-table} ("id", "uuid", "user", "ip", "active");


CREATE TABLE IF NOT EXISTS {iptempmutes-table} (
    "id"            SERIAL PRIMARY KEY                             NOT NULL,
    "uuid"          VARCHAR(36) REFERENCES {users-table} (uuid)    NOT NULL,
    "user"          VARCHAR(32)                                    NOT NULL,
    "ip"            VARCHAR(32)                                    NOT NULL,
    "time"          BIGINT                                         NOT NULL,
    "reason"        TEXT                                           NOT NULL,
    "server"        VARCHAR(32)                                    NOT NULL,
    "date"          TIMESTAMP DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    "active"        BOOLEAN                                        NOT NULL,
    "executed_by"   VARCHAR(64)                                    NOT NULL,
    "removed_by"    VARCHAR(32)
);

CREATE INDEX IF NOT EXISTS idx_iptempmutes ON {iptempmutes-table} ("id", "uuid", "user", "ip", "active");



CREATE TABLE IF NOT EXISTS {kicks-table} (
    "id"            SERIAL PRIMARY KEY                             NOT NULL,
    "uuid"          VARCHAR(36) REFERENCES {users-table} (uuid)    NOT NULL,
    "user"          VARCHAR(32)                                    NOT NULL,
    "ip"            VARCHAR(32)                                    NOT NULL,
    "reason"        TEXT                                           NOT NULL,
    "server"        VARCHAR(32)                                    NOT NULL,
    "date"          TIMESTAMP DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    "executed_by"   VARCHAR(64)                                    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_kicks ON {kicks-table} ("id", "uuid", "user", "ip");



CREATE TABLE IF NOT EXISTS {warns-table} (
    "id"            SERIAL PRIMARY KEY                             NOT NULL,
    "uuid"          VARCHAR(36) REFERENCES {users-table} (uuid)    NOT NULL,
    "user"          VARCHAR(32)                                    NOT NULL,
    "ip"            VARCHAR(32)                                    NOT NULL,
    "reason"        TEXT                                           NOT NULL,
    "server"        VARCHAR(32)                                    NOT NULL,
    "date"          TIMESTAMP DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    "executed_by"   VARCHAR(64)                                    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_warns ON {warns-table} ("id", "uuid", "user", "ip");