CREATE TABLE IF NOT EXISTS bu_users
(
    id          DATA_TYPE_SERIAL,
    uuid        DATA_TYPE_VARCHAR UNIQUE NOT NULL,
    username    DATA_TYPE_VARCHAR        NOT NULL,
    ip          DATA_TYPE_VARCHAR        NOT NULL,
    language    DATA_TYPE_VARCHAR        NOT NULL,
    firstlogin  DATA_TYPE_DATETIME       NOT NULL,
    lastlogout  DATA_TYPE_DATETIME       NOT NULL,
    joined_host TEXT,
    PRIMARY KEY (id)
);

CREATE INDEX idx_users_u ON bu_users (uuid);
CREATE INDEX idx_users_un ON bu_users (username);
CREATE INDEX idx_users_i ON bu_users (ip);

CREATE TABLE IF NOT EXISTS bu_ignoredusers
(
    `user`  DATA_TYPE_VARCHAR NOT NULL,
    ignored DATA_TYPE_VARCHAR NOT NULL,
    PRIMARY KEY (`user`, `ignored`)
);

CREATE TABLE IF NOT EXISTS bu_friendsettings
(
    `user`   DATA_TYPE_VARCHAR NOT NULL,
    requests DATA_TYPE_BOOLEAN NOT NULL,
    messages DATA_TYPE_BOOLEAN NOT NULL,
    PRIMARY KEY (`user`)
);

CREATE TABLE IF NOT EXISTS bu_friends
(
    `user`  DATA_TYPE_VARCHAR NOT NULL,
    friend  DATA_TYPE_VARCHAR NOT NULL,
    created DATA_TYPE_DATETIME,
    PRIMARY KEY (`user`, friend)
);

CREATE TABLE IF NOT EXISTS bu_friendrequests
(
    `user`       DATA_TYPE_VARCHAR NOT NULL,
    friend       DATA_TYPE_VARCHAR NOT NULL,
    requested_at DATA_TYPE_DATETIME,
    PRIMARY KEY (`user`, friend)
);

CREATE TABLE IF NOT EXISTS bu_bans
(
    id                      DATA_TYPE_SERIAL,
    uuid                    DATA_TYPE_VARCHAR  NOT NULL,
    `user`                  DATA_TYPE_VARCHAR  NOT NULL,
    ip                      DATA_TYPE_VARCHAR  NOT NULL,
    reason                  TEXT               NOT NULL,
    server                  DATA_TYPE_VARCHAR  NOT NULL,
    date                    DATA_TYPE_DATETIME NOT NULL,
    active                  DATA_TYPE_BOOLEAN  NOT NULL,
    executed_by             DATA_TYPE_VARCHAR  NOT NULL,
    duration                DATA_TYPE_BIGINT   NOT NULL,
    type                    DATA_TYPE_VARCHAR  NOT NULL,
    removed                 DATA_TYPE_BOOLEAN  NOT NULL DEFAULT_VALUE_BOOLEAN_FALSE,
    removed_by              DATA_TYPE_VARCHAR,
    removed_at              DATA_TYPE_DATETIME NULL DEFAULT NULL,
    punishmentaction_status DATA_TYPE_BOOLEAN  NOT NULL DEFAULT_VALUE_BOOLEAN_FALSE,
    PRIMARY KEY (id)
);

CREATE INDEX idx_bans_e ON bu_bans (executed_by);

CREATE INDEX idx_bans_ut ON bu_bans (uuid, type);
CREATE INDEX idx_bans_uat ON bu_bans (uuid, active, type);
CREATE INDEX idx_bans_uast ON bu_bans (uuid, active, server, type);

CREATE INDEX idx_bans_it ON bu_bans (ip, type);
CREATE INDEX idx_bans_iat ON bu_bans (ip, active, type);
CREATE INDEX idx_bans_iast ON bu_bans (ip, active, server, type);

CREATE TABLE IF NOT EXISTS bu_mutes
(
    id                      DATA_TYPE_SERIAL,
    uuid                    DATA_TYPE_VARCHAR  NOT NULL,
    `user`                  DATA_TYPE_VARCHAR  NOT NULL,
    ip                      DATA_TYPE_VARCHAR  NOT NULL,
    reason                  TEXT               NOT NULL,
    server                  DATA_TYPE_VARCHAR  NOT NULL,
    date                    DATA_TYPE_DATETIME NOT NULL,
    active                  DATA_TYPE_BOOLEAN  NOT NULL,
    executed_by             DATA_TYPE_VARCHAR  NOT NULL,
    duration                DATA_TYPE_BIGINT   NOT NULL,
    type                    DATA_TYPE_VARCHAR  NOT NULL,
    removed                 DATA_TYPE_BOOLEAN  NOT NULL DEFAULT_VALUE_BOOLEAN_FALSE,
    removed_by              DATA_TYPE_VARCHAR,
    removed_at              DATA_TYPE_DATETIME NULL DEFAULT NULL,
    punishmentaction_status DATA_TYPE_BOOLEAN  NOT NULL DEFAULT_VALUE_BOOLEAN_FALSE,
    PRIMARY KEY (id)
);

CREATE INDEX idx_mutes_e ON bu_mutes (executed_by);

CREATE INDEX idx_mutes_ut ON bu_mutes (uuid, type);
CREATE INDEX idx_mutes_uat ON bu_mutes (uuid, active, type);
CREATE INDEX idx_mutes_uast ON bu_mutes (uuid, active, server, type);

CREATE INDEX idx_mutes_it ON bu_mutes (ip, type);
CREATE INDEX idx_mutes_iat ON bu_mutes (ip, active, type);
CREATE INDEX idx_mutes_iast ON bu_mutes (ip, active, server, type);

CREATE TABLE IF NOT EXISTS bu_kicks
(
    id                      DATA_TYPE_SERIAL,
    uuid                    DATA_TYPE_VARCHAR  NOT NULL,
    `user`                  DATA_TYPE_VARCHAR  NOT NULL,
    ip                      DATA_TYPE_VARCHAR  NOT NULL,
    reason                  TEXT               NOT NULL,
    server                  DATA_TYPE_VARCHAR  NOT NULL,
    date                    DATA_TYPE_DATETIME NOT NULL,
    executed_by             DATA_TYPE_VARCHAR  NOT NULL,
    punishmentaction_status DATA_TYPE_BOOLEAN  NOT NULL DEFAULT_VALUE_BOOLEAN_FALSE,
    PRIMARY KEY (id)
);

CREATE INDEX idx_kicks_u ON bu_kicks (uuid);
CREATE INDEX idx_kicks_e ON bu_kicks (executed_by);

CREATE TABLE IF NOT EXISTS bu_warns
(
    id                      DATA_TYPE_SERIAL,
    uuid                    DATA_TYPE_VARCHAR  NOT NULL,
    `user`                  DATA_TYPE_VARCHAR  NOT NULL,
    ip                      DATA_TYPE_VARCHAR  NOT NULL,
    reason                  TEXT               NOT NULL,
    server                  DATA_TYPE_VARCHAR  NOT NULL,
    date                    DATA_TYPE_DATETIME NOT NULL,
    executed_by             DATA_TYPE_VARCHAR  NOT NULL,
    punishmentaction_status DATA_TYPE_BOOLEAN  NOT NULL DEFAULT_VALUE_BOOLEAN_FALSE,
    PRIMARY KEY (id)
);

CREATE INDEX idx_warns_u ON bu_warns (uuid);
CREATE INDEX idx_warns_e ON bu_warns (executed_by);

CREATE TABLE IF NOT EXISTS bu_punishmentactions
(
    id       DATA_TYPE_SERIAL,
    uuid     DATA_TYPE_VARCHAR  NOT NULL,
    `user`   DATA_TYPE_VARCHAR  NOT NULL,
    ip       DATA_TYPE_VARCHAR  NOT NULL,
    actionid DATA_TYPE_VARCHAR  NOT NULL,
    date     DATA_TYPE_DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bu_reports
(
    id          DATA_TYPE_SERIAL,
    uuid        DATA_TYPE_VARCHAR  NOT NULL,
    reported_by DATA_TYPE_VARCHAR  NOT NULL,
    date        DATA_TYPE_DATETIME NOT NULL,
    handled     DATA_TYPE_BOOLEAN  NOT NULL,
    server      DATA_TYPE_VARCHAR  NOT NULL,
    reason      TEXT               NOT NULL,
    accepted    DATA_TYPE_BOOLEAN,
    PRIMARY KEY (id)
);

CREATE INDEX idx_reports_u ON bu_reports (uuid);
CREATE INDEX idx_reports_ud ON bu_reports (uuid, date);
CREATE INDEX idx_reports_urb ON bu_reports (uuid, reported_by);

CREATE TABLE IF NOT EXISTS bu_messagequeue
(
    id      DATA_TYPE_SERIAL,
    `user`  DATA_TYPE_VARCHAR  NOT NULL,
    message TEXT               NOT NULL,
    date    DATA_TYPE_DATETIME NOT NULL,
    type    DATA_TYPE_VARCHAR  NOT NULL,
    active  DATA_TYPE_BOOLEAN  NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_messagequeue_u ON bu_messagequeue (`user`, type);