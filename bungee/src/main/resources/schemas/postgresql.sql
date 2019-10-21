CREATE TABLE IF NOT EXISTS "{users-table}"
(
    id         BIGSERIAL PRIMARY KEY,
    uuid       VARCHAR(48) UNIQUE NOT NULL,
    username   VARCHAR(32)        NOT NULL,
    ip         VARCHAR(32)        NOT NULL,
    language   VARCHAR(24)        NOT NULL,
    firstlogin TIMESTAMP          NOT NULL,
    lastlogout TIMESTAMP          NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users ON "{users-table}" (id, uuid, username, ip);

CREATE TABLE IF NOT EXISTS "{ignoredusers-table}"
(
    "user"  VARCHAR(48) NOT NULL,
    ignored VARCHAR(48) NOT NULL,
    PRIMARY KEY ("user", ignored)
);

CREATE TABLE IF NOT EXISTS "{friendsettings-table}"
(
    "user"   VARCHAR(48) PRIMARY KEY NOT NULL,
    requests BOOLEAN                 NOT NULL,
    messages BOOLEAN                 NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_friendset ON "{friendsettings-table}" ("user", requests, messages);

CREATE TABLE IF NOT EXISTS "{friends-table}"
(
    "user"  VARCHAR(48)                         NOT NULL,
    friend  VARCHAR(48)                         NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY ("user", friend)
);

CREATE INDEX IF NOT EXISTS idx_friends ON "{friends-table}" ("user", friend);

CREATE TABLE IF NOT EXISTS "{friendrequests-table}"
(
    "user"       VARCHAR(48)                         NOT NULL,
    friend       VARCHAR(48)                         NOT NULL,
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY ("user", friend)
);

CREATE INDEX IF NOT EXISTS idx_friendreq ON "{friendrequests-table}" ("user", friend);

CREATE TABLE IF NOT EXISTS "{bans-table}"
(
    id                      BIGSERIAL PRIMARY KEY,
    uuid                    VARCHAR(48) NOT NULL,
    "user"                  VARCHAR(32) NOT NULL,
    ip                      VARCHAR(32) NOT NULL,
    reason                  TEXT        NOT NULL,
    server                  VARCHAR(32) NOT NULL,
    date                    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active                  BOOLEAN     NOT NULL,
    executed_by             VARCHAR(64) NOT NULL,
    duration                BIGINT      NOT NULL,
    type                    VARCHAR(16) NOT NULL,
    removed                 BOOLEAN     NOT NULL DEFAULT FALSE,
    removed_by              VARCHAR(32),
    punishmentaction_status BOOLEAN     NOT NULL DEFAULT FALSE,
    FOREIGN KEY (uuid) REFERENCES "{users-table}" (uuid)
);

CREATE INDEX IF NOT EXISTS idx_bans ON "{bans-table}" (id, uuid, "user", ip, active, server);

CREATE TABLE IF NOT EXISTS "{mutes-table}"
(
    id                      BIGSERIAL PRIMARY KEY,
    uuid                    VARCHAR(48) NOT NULL,
    "user"                  VARCHAR(32) NOT NULL,
    ip                      VARCHAR(32) NOT NULL,
    reason                  TEXT        NOT NULL,
    server                  VARCHAR(32) NOT NULL,
    date                    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active                  BOOLEAN     NOT NULL,
    executed_by             VARCHAR(64) NOT NULL,
    duration                BIGINT      NOT NULL,
    type                    VARCHAR(16) NOT NULL,
    removed                 BOOLEAN     NOT NULL DEFAULT FALSE,
    removed_by              VARCHAR(32),
    punishmentaction_status BOOLEAN     NOT NULL DEFAULT FALSE,
    FOREIGN KEY (uuid) REFERENCES "{users-table}" (uuid)
);

CREATE INDEX IF NOT EXISTS idx_mutes ON "{mutes-table}" (id, uuid, "user", ip, active, server);


CREATE TABLE IF NOT EXISTS "{kicks-table}"
(
    id                      BIGSERIAL PRIMARY KEY,
    uuid                    VARCHAR(36) NOT NULL,
    "user"                  VARCHAR(32) NOT NULL,
    ip                      VARCHAR(32) NOT NULL,
    reason                  TEXT        NOT NULL,
    server                  VARCHAR(32) NOT NULL,
    date                    TIMESTAMP            DEFAULT CURRENT_TIMESTAMP NOT NULL,
    executed_by             VARCHAR(64) NOT NULL,
    punishmentaction_status BOOLEAN     NOT NULL DEFAULT FALSE,
    FOREIGN KEY (uuid) REFERENCES "{users-table}" (uuid)
);

CREATE INDEX IF NOT EXISTS idx_kicks ON "{kicks-table}" (id, uuid, "user", ip);

CREATE TABLE IF NOT EXISTS "{warns-table}"
(
    id                      BIGSERIAL PRIMARY KEY,
    uuid                    VARCHAR(36) NOT NULL,
    "user"                  VARCHAR(32) NOT NULL,
    ip                      VARCHAR(32) NOT NULL,
    reason                  TEXT        NOT NULL,
    server                  VARCHAR(32) NOT NULL,
    date                    TIMESTAMP            DEFAULT CURRENT_TIMESTAMP NOT NULL,
    executed_by             VARCHAR(64) NOT NULL,
    punishmentaction_status BOOLEAN     NOT NULL DEFAULT FALSE,
    FOREIGN KEY (uuid) REFERENCES "{users-table}" (uuid)
);

CREATE INDEX IF NOT EXISTS idx_warns ON "{warns-table}" (id, uuid, "user", ip);

CREATE TABLE IF NOT EXISTS "{punishmentactions-table}"
(
    id       BIGSERIAL PRIMARY KEY,
    uuid     VARCHAR(36)                         NOT NULL,
    "user"   VARCHAR(32)                         NOT NULL,
    ip       VARCHAR(32)                         NOT NULL,
    actionid VARCHAR(36)                         NOT NULL,
    date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (uuid) REFERENCES "{users-table}" (uuid)
);

CREATE INDEX IF NOT EXISTS idx_punishactions ON "{punishmentactions-table}" (id, uuid, "user", ip, actionid);

CREATE TABLE IF NOT EXISTS "{reports-table}"
(
    id          BIGSERIAL PRIMARY KEY,
    uuid        VARCHAR(36)                         NOT NULL,
    reported_by VARCHAR(32)                         NOT NULL,
    date        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    handled     BOOLEAN                             NOT NULL,
    server      VARCHAR(64)                         NOT NULL,
    reason      TEXT                                NOT NULL,
    accepted    BOOLEAN,
    FOREIGN KEY (uuid) REFERENCES "{users-table}" (uuid)
);

CREATE INDEX IF NOT EXISTS idx_reports ON "{reports-table}" (id, uuid, reported_by, handled);

CREATE TABLE IF NOT EXISTS "{messagequeue-table}"
(
    id      BIGSERIAL PRIMARY KEY,
    "user"  VARCHAR(36)                         NOT NULL,
    message TEXT                                NOT NULL,
    date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    type    VARCHAR(16)                         NOT NULL,
    active  BOOLEAN                             NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_messagequeue ON "{messagequeue-table}" (id, "user", type, date);