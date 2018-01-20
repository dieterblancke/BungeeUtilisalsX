CREATE TABLE IF NOT EXISTS {users-table} (
    id        INT(11) AUTO_INCREMENT            NOT NULL,
    uuid      VARCHAR(36) UNIQUE                NOT NULL,
    username  VARCHAR(32) UNIQUE                NOT NULL,
    ip        VARCHAR(32)                       NOT NULL,
    language  VARCHAR(24)                       NOT NULL,
    PRIMARY KEY (id),
    KEY idx_users (id, uuid, username, ip)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {friends-table} (
    userid       INT(11)                              NOT NULL,
    friendid     INT(11)                              NOT NULL,
    friendsince  DATETIME DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    PRIMARY KEY (userid, friendid),
    KEY idx_friends (userid, friendid),
    FOREIGN KEY (userid)    REFERENCES {users-table} (id),
    FOREIGN KEY (friendid)  REFERENCES {users-table} (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {friendrequests-table} (
    userid        INT(11)                              NOT NULL,
    friendid      INT(11)                              NOT NULL,
    requested_at  DATETIME DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    PRIMARY KEY (userid, friendid),
    KEY idx_friendreq (userid, friendid),
    FOREIGN KEY (userid)    REFERENCES {users-table} (id),
    FOREIGN KEY (friendid)  REFERENCES {users-table} (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {bans-table} (
    id           INT(11) AUTO_INCREMENT                  NOT NULL,
    uuid         VARCHAR(36)                             NOT NULL,
    user         VARCHAR(32)                             NOT NULL,
    ip           VARCHAR(32)                             NOT NULL,
    reason       TEXT                                    NOT NULL,
    server       VARCHAR(32)                             NOT NULL,
    date         DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    active       TINYINT(1)                              NOT NULL,
    executed_by  VARCHAR(64)                             NOT NULL,
    removed_by   VARCHAR(32),
    PRIMARY KEY (id),
    KEY idx_bans (id, uuid, user, ip, active),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {ipbans-table} (
    id           INT(11) AUTO_INCREMENT                  NOT NULL,
    uuid         VARCHAR(36)                             NOT NULL,
    user         VARCHAR(32)                             NOT NULL,
    ip           VARCHAR(32)                             NOT NULL,
    reason       TEXT                                    NOT NULL,
    server       VARCHAR(32)                             NOT NULL,
    date         DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    active       TINYINT(1)                              NOT NULL,
    executed_by  VARCHAR(64)                             NOT NULL,
    removed_by   VARCHAR(32),
    PRIMARY KEY (id),
    KEY idx_ipbans (id, uuid, user, ip, active),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {tempbans-table} (
    id           INT(11) AUTO_INCREMENT                  NOT NULL,
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
    PRIMARY KEY (id),
    KEY idx_tempbans (id, uuid, user, ip, active),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {iptempbans-table} (
    id           INT(11) AUTO_INCREMENT                  NOT NULL,
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
    PRIMARY KEY (id),
    KEY idx_iptempbans (id, uuid, user, ip, active),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {mutes-table} (
    id            INT(11) AUTO_INCREMENT                  NOT NULL,
    uuid          VARCHAR(36)                             NOT NULL,
    user          VARCHAR(32)                             NOT NULL,
    ip            VARCHAR(32)                             NOT NULL,
    reason        TEXT                                    NOT NULL,
    server        VARCHAR(32)                             NOT NULL,
    date          DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    active        TINYINT(1)                              NOT NULL,
    executed_by   VARCHAR(64)                             NOT NULL,
    removed_by    VARCHAR(32),
    PRIMARY KEY (id),
    KEY idx_mutes (id, uuid, user, ip, active),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {ipmutes-table} (
    id            INT(11) AUTO_INCREMENT                  NOT NULL,
    uuid          VARCHAR(36)                             NOT NULL,
    user          VARCHAR(32)                             NOT NULL,
    ip            VARCHAR(32)                             NOT NULL,
    reason        TEXT                                    NOT NULL,
    server        VARCHAR(32)                             NOT NULL,
    date          DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    active        TINYINT(1)                              NOT NULL,
    executed_by   VARCHAR(64)                             NOT NULL,
    removed_by    VARCHAR(32),
    PRIMARY KEY (id),
    KEY idx_ipmutes (id, uuid, user, ip, active),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {tempmutes-table} (
    id            INT(11) AUTO_INCREMENT                  NOT NULL,
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
    PRIMARY KEY (id),
    KEY idx_tempmutes (id, uuid, user, ip, active),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {iptempmutes-table} (
    id            INT(11) AUTO_INCREMENT                  NOT NULL,
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
    PRIMARY KEY (id),
    KEY idx_iptempbans (id, uuid, user, ip, active),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {kicks-table} (
    id            INT(11) AUTO_INCREMENT                  NOT NULL,
    uuid          VARCHAR(36)                             NOT NULL,
    user          VARCHAR(32)                             NOT NULL,
    ip            VARCHAR(32)                             NOT NULL,
    reason        TEXT                                    NOT NULL,
    server        VARCHAR(32)                             NOT NULL,
    date          DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    executed_by   VARCHAR(64)                             NOT NULL,
    PRIMARY KEY (id),
    KEY idx_kicks (id, uuid, user, ip),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {warns-table} (
    id            INT(11) AUTO_INCREMENT                  NOT NULL,
    uuid          VARCHAR(36)                             NOT NULL,
    user          VARCHAR(32)                             NOT NULL,
    ip            VARCHAR(32)                             NOT NULL,
    reason        TEXT                                    NOT NULL,
    server        VARCHAR(32)                             NOT NULL,
    date          DATETIME DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    executed_by   VARCHAR(64)                             NOT NULL,
    PRIMARY KEY (id),
    KEY idx_warns (id, uuid, user, ip),
    FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;