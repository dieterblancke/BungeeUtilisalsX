CREATE TABLE IF NOT EXISTS {users-table} (
    id        INT(11) AUTO_INCREMENT     NOT NULL,
    uuid      VARCHAR(36)                NOT NULL,
    username  VARCHAR(32)                NOT NULL,
    ip        VARCHAR(32)                NOT NULL,
    language  VARCHAR(24)                NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (uuid),
    UNIQUE KEY (username),
    KEY idx_users_uuid (uuid),
    KEY idx_users_user (username),
    KEY idx_users_ip (ip)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {friends-table} (
    userid       INT(11)                              NOT NULL,
    friendid     INT(11)                              NOT NULL,
    friendsince  DATETIME DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    PRIMARY KEY (userid, friendid),
    KEY idx_friends_id (userid),
    KEY idx_friends_fid (friendid),
    FOREIGN KEY(userid) REFERENCES {users-table}(id),
    FOREIGN KEY(friendid) REFERENCES {users-table}(id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS {friendrequests-table} (
    userid        INT(11)                              NOT NULL,
    friendid      INT(11)                              NOT NULL,
    requested_at  DATETIME DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    PRIMARY KEY (userid, friendid),
    KEY idx_friends_id (userid),
    KEY idx_friends_fid (friendid),
    FOREIGN KEY(userid) REFERENCES {users-table}(id),
    FOREIGN KEY(friendid) REFERENCES {users-table}(id)
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
    KEY idx_bans_uuid (uuid),
    KEY idx_bans_user (user),
    KEY idx_bans_active (active),
    KEY idx_bans_ip (ip),
    FOREIGN KEY (uuid) REFERENCES {users-table}(uuid)
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
    KEY idx_ipbans_uuid (uuid),
    KEY idx_ipbans_user (user),
    KEY idx_ipbans_active (active),
    KEY idx_ipbans_ip (ip),
    FOREIGN KEY (uuid) REFERENCES {users-table}(uuid)
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
    KEY idx_tempbans_uuid (uuid),
    KEY idx_tempbans_user (user),
    KEY idx_tempbans_active (active),
    KEY idx_tempbans_ip (ip),
    FOREIGN KEY (uuid) REFERENCES {users-table}(uuid)
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
    KEY idx_iptempbans_uuid (uuid),
    KEY idx_iptempbans_user (user),
    KEY idx_iptempbans_active (active),
    KEY idx_iptempbans_ip (ip),
    FOREIGN KEY (uuid) REFERENCES {users-table}(uuid)
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
    KEY idx_mutes_uuid (uuid),
    KEY idx_mutes_user (user),
    KEY idx_mutes_active (active),
    KEY idx_mutes_ip (ip),
    FOREIGN KEY (uuid) REFERENCES {users-table}(uuid)
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
    KEY idx_ipmutes_uuid (uuid),
    KEY idx_ipmutes_user (user),
    KEY idx_ipmutes_active (active),
    KEY idx_ipmutes_ip (ip),
    FOREIGN KEY (uuid) REFERENCES {users-table}(uuid)
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
    KEY idx_tempmutes_uuid (uuid),
    KEY idx_tempmutes_user (user),
    KEY idx_tempmutes_active (active),
    KEY idx_tempmutes_ip (ip),
    FOREIGN KEY (uuid) REFERENCES {users-table}(uuid)
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
    KEY idx_iptempbans_uuid (uuid),
    KEY idx_iptempbans_user (user),
    KEY idx_iptempbans_active (active),
    KEY idx_iptempbans_ip (ip),
    FOREIGN KEY (uuid) REFERENCES {users-table}(uuid)
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
    KEY idx_kicks_uuid (uuid),
    KEY idx_kicks_user (user),
    KEY idx_kicks_ip (ip),
    FOREIGN KEY (uuid) REFERENCES {users-table}(uuid)
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
    KEY idx_warns_uuid (uuid),
    KEY idx_warns_user (user),
    KEY idx_warns_ip (ip),
    FOREIGN KEY (uuid) REFERENCES {users-table}(uuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;