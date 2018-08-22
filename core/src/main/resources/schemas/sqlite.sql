CREATE TABLE IF NOT EXISTS {users-table} (
    id         INTEGER PRIMARY KEY AUTOINCREMENT      NOT NULL,
    uuid       VARCHAR(36) UNIQUE                     NOT NULL,
    username   VARCHAR(32) UNIQUE                     NOT NULL,
    ip         VARCHAR(32)                            NOT NULL,
    language   VARCHAR(24)                            NOT NULL,
    firstlogin DATETIME                               NOT NULL,
    lastlogout DATETIME                               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users ON {users-table} (uuid, username, ip);



CREATE TABLE IF NOT EXISTS {friends-table} (
    userid       INTEGER                              NOT NULL,
    friendid     INTEGER                              NOT NULL,
    friendsince  DATETIME DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    PRIMARY KEY (userid, friendid),
    FOREIGN KEY (userid)    REFERENCES {users-table} (id),
    FOREIGN KEY (friendid)  REFERENCES {users-table} (id)
);

CREATE INDEX IF NOT EXISTS idx_friends ON {friends-table} (userid, friendid);



CREATE TABLE IF NOT EXISTS {friendrequests-table} (
    userid        INTEGER                              NOT NULL,
    friendid      INTEGER                              NOT NULL,
    requested_at  DATETIME DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    PRIMARY KEY (userid, friendid),
    FOREIGN KEY (userid)    REFERENCES {users-table} (id),
    FOREIGN KEY (friendid)  REFERENCES {users-table} (id)
);

CREATE INDEX IF NOT EXISTS idx_friendreq ON {friends-table} (userid, friendid);



CREATE TABLE IF NOT EXISTS {bans-table} (
    id           INTEGER PRIMARY KEY AUTOINCREMENT       NOT NULL,
    uuid         VARCHAR(36)                             NOT NULL,
    user         VARCHAR(32)                             NOT NULL,
    ip           VARCHAR(32)                             NOT NULL,
    reason       TEXT                                    NOT NULL,
    server       VARCHAR(32)                             NOT NULL,
    date         DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    active       TINYINT(1)                              NOT NULL,
    executed_by  VARCHAR(64)                             NOT NULL,
    removed_by   VARCHAR(32),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

CREATE INDEX IF NOT EXISTS idx_bans ON {bans-table} (id, uuid, user, ip, active);



CREATE TABLE IF NOT EXISTS {ipbans-table} (
    id           INTEGER PRIMARY KEY AUTOINCREMENT       NOT NULL,
    uuid         VARCHAR(36)                             NOT NULL,
    user         VARCHAR(32)                             NOT NULL,
    ip           VARCHAR(32)                             NOT NULL,
    reason       TEXT                                    NOT NULL,
    server       VARCHAR(32)                             NOT NULL,
    date         DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    active       TINYINT(1)                              NOT NULL,
    executed_by  VARCHAR(64)                             NOT NULL,
    removed_by   VARCHAR(32),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

CREATE INDEX IF NOT EXISTS idx_ipbans ON {ipbans-table} (id, uuid, user, ip, active);



CREATE TABLE IF NOT EXISTS {tempbans-table} (
    id           INTEGER PRIMARY KEY AUTOINCREMENT       NOT NULL,
    uuid         VARCHAR(36)                             NOT NULL,
    user         VARCHAR(32)                             NOT NULL,
    ip           VARCHAR(32)                             NOT NULL,
    time         LONG                                    NOT NULL,
    reason       TEXT                                    NOT NULL,
    server       VARCHAR(32)                             NOT NULL,
    date         DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    active       TINYINT(1)                              NOT NULL,
    executed_by  VARCHAR(64)                             NOT NULL,
    removed_by   VARCHAR(32),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

CREATE INDEX IF NOT EXISTS idx_tempbans ON {tempbans-table} (id, uuid, user, ip, active);



CREATE TABLE IF NOT EXISTS {iptempbans-table} (
    id           INTEGER PRIMARY KEY AUTOINCREMENT       NOT NULL,
    uuid         VARCHAR(36)                             NOT NULL,
    user         VARCHAR(32)                             NOT NULL,
    ip           VARCHAR(32)                             NOT NULL,
    time         LONG                                    NOT NULL,
    reason       TEXT                                    NOT NULL,
    server       VARCHAR(32)                             NOT NULL,
    date         DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    active       TINYINT(1)                              NOT NULL,
    executed_by  VARCHAR(64)                             NOT NULL,
    removed_by   VARCHAR(32),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

CREATE INDEX IF NOT EXISTS idx_iptempbans ON {iptempbans-table} (id, uuid, user, ip, active);



CREATE TABLE IF NOT EXISTS {mutes-table} (
    id            INTEGER PRIMARY KEY AUTOINCREMENT       NOT NULL,
    uuid          VARCHAR(36)                             NOT NULL,
    user          VARCHAR(32)                             NOT NULL,
    ip            VARCHAR(32)                             NOT NULL,
    reason        TEXT                                    NOT NULL,
    server        VARCHAR(32)                             NOT NULL,
    date          DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    active        TINYINT(1)                              NOT NULL,
    executed_by   VARCHAR(64)                             NOT NULL,
    removed_by    VARCHAR(32),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

CREATE INDEX IF NOT EXISTS idx_mutes ON {mutes-table} (id, uuid, user, ip, active);



CREATE TABLE IF NOT EXISTS {ipmutes-table} (
    id            INTEGER PRIMARY KEY AUTOINCREMENT       NOT NULL,
    uuid          VARCHAR(36)                             NOT NULL,
    user          VARCHAR(32)                             NOT NULL,
    ip            VARCHAR(32)                             NOT NULL,
    reason        TEXT                                    NOT NULL,
    server        VARCHAR(32)                             NOT NULL,
    date          DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    active        TINYINT(1)                              NOT NULL,
    executed_by   VARCHAR(64)                             NOT NULL,
    removed_by    VARCHAR(32),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

CREATE INDEX IF NOT EXISTS idx_ipmutes ON {ipmutes-table} (id, uuid, user, ip, active);



CREATE TABLE IF NOT EXISTS {tempmutes-table} (
    id            INTEGER PRIMARY KEY AUTOINCREMENT       NOT NULL,
    uuid          VARCHAR(36)                             NOT NULL,
    user          VARCHAR(32)                             NOT NULL,
    ip            VARCHAR(32)                             NOT NULL,
    time          LONG                                    NOT NULL,
    reason        TEXT                                    NOT NULL,
    server        VARCHAR(32)                             NOT NULL,
    date          DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    active        TINYINT(1)                              NOT NULL,
    executed_by   VARCHAR(64)                             NOT NULL,
    removed_by    VARCHAR(32),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

CREATE INDEX IF NOT EXISTS idx_tempmutes ON {tempmutes-table} (id, uuid, user, ip, active);


CREATE TABLE IF NOT EXISTS {iptempmutes-table} (
    id            INTEGER PRIMARY KEY AUTOINCREMENT       NOT NULL,
    uuid          VARCHAR(36)                             NOT NULL,
    user          VARCHAR(32)                             NOT NULL,
    ip            VARCHAR(32)                             NOT NULL,
    time          LONG                                    NOT NULL,
    reason        TEXT                                    NOT NULL,
    server        VARCHAR(32)                             NOT NULL,
    date          DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    active        TINYINT(1)                              NOT NULL,
    executed_by   VARCHAR(64)                             NOT NULL,
    removed_by    VARCHAR(32),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

CREATE INDEX IF NOT EXISTS idx_iptempmutes ON {iptempmutes-table} (id, uuid, user, ip, active);



CREATE TABLE IF NOT EXISTS {kicks-table} (
    id            INTEGER PRIMARY KEY AUTOINCREMENT       NOT NULL,
    uuid          VARCHAR(36)                             NOT NULL,
    user          VARCHAR(32)                             NOT NULL,
    ip            VARCHAR(32)                             NOT NULL,
    reason        TEXT                                    NOT NULL,
    server        VARCHAR(32)                             NOT NULL,
    date          DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    executed_by   VARCHAR(64)                             NOT NULL,
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

CREATE INDEX IF NOT EXISTS idx_kicks ON {kicks-table} (id, uuid, user, ip);



CREATE TABLE IF NOT EXISTS {warns-table} (
    id            INTEGER PRIMARY KEY AUTOINCREMENT       NOT NULL,
    uuid          VARCHAR(36)                             NOT NULL,
    user          VARCHAR(32)                             NOT NULL,
    ip            VARCHAR(32)                             NOT NULL,
    reason        TEXT                                    NOT NULL,
    server        VARCHAR(32)                             NOT NULL,
    date          DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    executed_by   VARCHAR(64)                             NOT NULL,
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

CREATE INDEX IF NOT EXISTS idx_warns ON {warns-table} (id, uuid, user, ip);