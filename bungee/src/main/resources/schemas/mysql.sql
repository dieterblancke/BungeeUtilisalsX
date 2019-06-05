CREATE TABLE IF NOT EXISTS `{users-table}`
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid       VARCHAR(48) UNIQUE NOT NULL,
    username   VARCHAR(32)        NOT NULL,
    ip         VARCHAR(32)        NOT NULL,
    language   VARCHAR(24)        NOT NULL,
    firstlogin DATETIME           NOT NULL,
    lastlogout DATETIME           NOT NULL
) DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_users ON `{users-table}` (id, uuid, username, ip);

CREATE TABLE IF NOT EXISTS `{ignoredusers-table}`
(
    user    BIGINT NOT NULL,
    ignored BIGINT NOT NULL,
    PRIMARY KEY (user, ignored),
    FOREIGN KEY (user) REFERENCES `{users-table}` (id),
    FOREIGN KEY (ignored) REFERENCES `{users-table}` (id)
) DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `{friendsettings-table}`
(
    user     VARCHAR(48) PRIMARY KEY NOT NULL,
    requests BOOLEAN                 NOT NULL,
    messages BOOLEAN                 NOT NULL
) DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_friendset ON `{friendsettings-table}` (user, requests, messages);

CREATE TABLE IF NOT EXISTS `{friends-table}`
(
    user    VARCHAR(48)                        NOT NULL,
    friend  VARCHAR(48)                        NOT NULL,
    created DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (user, friend)
) DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_friends ON `{friends-table}` (user, friend);

CREATE TABLE IF NOT EXISTS `{friendrequests-table}`
(
    user         VARCHAR(48)                        NOT NULL,
    friend       VARCHAR(48)                        NOT NULL,
    requested_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (user, friend)
) DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_friendreq ON `{friendrequests-table}` (user, friend);

CREATE TABLE IF NOT EXISTS `{bans-table}`
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid        VARCHAR(48) NOT NULL,
    user        VARCHAR(32) NOT NULL,
    ip          VARCHAR(32) NOT NULL,
    reason      TEXT        NOT NULL,
    server      VARCHAR(32) NOT NULL,
    date        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active      BOOLEAN     NOT NULL,
    executed_by VARCHAR(64) NOT NULL,
    duration    LONG        NOT NULL,
    type        VARCHAR(16) NOT NULL,
    removed     BOOLEAN     NOT NULL DEFAULT 0,
    removed_by  VARCHAR(32),
    FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
) DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_bans ON `{bans-table}` (id, uuid, user, ip, active, server);

CREATE TABLE IF NOT EXISTS `{mutes-table}`
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid        VARCHAR(48) NOT NULL,
    user        VARCHAR(32) NOT NULL,
    ip          VARCHAR(32) NOT NULL,
    reason      TEXT        NOT NULL,
    server      VARCHAR(32) NOT NULL,
    date        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active      BOOLEAN     NOT NULL,
    executed_by VARCHAR(64) NOT NULL,
    duration    LONG        NOT NULL,
    type        VARCHAR(16) NOT NULL,
    removed     BOOLEAN     NOT NULL DEFAULT 0,
    removed_by  VARCHAR(32),
    FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
) DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_mutes ON `{mutes-table}` (id, uuid, user, ip, active, server);


CREATE TABLE IF NOT EXISTS `{kicks-table}`
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid        VARCHAR(36)                        NOT NULL,
    user        VARCHAR(32)                        NOT NULL,
    ip          VARCHAR(32)                        NOT NULL,
    reason      TEXT                               NOT NULL,
    server      VARCHAR(32)                        NOT NULL,
    date        DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    executed_by VARCHAR(64)                        NOT NULL,
    FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
) DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_kicks ON `{kicks-table}` (id, uuid, user, ip);

CREATE TABLE IF NOT EXISTS `{warns-table}`
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid        VARCHAR(36)                        NOT NULL,
    user        VARCHAR(32)                        NOT NULL,
    ip          VARCHAR(32)                        NOT NULL,
    reason      TEXT                               NOT NULL,
    server      VARCHAR(32)                        NOT NULL,
    date        DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    executed_by VARCHAR(64)                        NOT NULL,
    FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
) DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_warns ON `{warns-table}` (id, uuid, user, ip);

CREATE TABLE IF NOT EXISTS `{punishmentactions-table}`
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid     VARCHAR(36)                        NOT NULL,
    user     VARCHAR(32)                        NOT NULL,
    ip       VARCHAR(32)                        NOT NULL,
    actionid VARCHAR(36)                        NOT NULL,
    date     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
) DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_punishactions ON `{punishmentactions-table}` (id, uuid, user, ip, actionid);