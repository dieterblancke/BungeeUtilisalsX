CREATE TABLE IF NOT EXISTS {users-table}
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

CREATE INDEX idx_users_u ON {users-table} (uuid);
CREATE INDEX idx_users_un ON {users-table} (username);
CREATE INDEX idx_users_i ON {users-table} (ip);

CREATE TABLE IF NOT EXISTS {ignoredusers-table}
(
    user    DATA_TYPE_VARCHAR NOT NULL,
    ignored DATA_TYPE_VARCHAR NOT NULL,
    PRIMARY KEY (user, ignored)
);

CREATE TABLE IF NOT EXISTS {friendsettings-table}
(
    user     DATA_TYPE_VARCHAR NOT NULL,
    requests DATA_TYPE_BOOLEAN NOT NULL,
    messages DATA_TYPE_BOOLEAN NOT NULL,
    PRIMARY KEY (user)
);

CREATE TABLE IF NOT EXISTS {friends-table}
(
    user    DATA_TYPE_VARCHAR NOT NULL,
    friend  DATA_TYPE_VARCHAR NOT NULL,
    created DATA_TYPE_DATETIME,
    PRIMARY KEY (user, friend)
);

CREATE TABLE IF NOT EXISTS {friendrequests-table}
(
    user         DATA_TYPE_VARCHAR NOT NULL,
    friend       DATA_TYPE_VARCHAR NOT NULL,
    requested_at DATA_TYPE_DATETIME,
    PRIMARY KEY (user, friend)
);

CREATE TABLE IF NOT EXISTS {bans-table}
(
    id                      DATA_TYPE_SERIAL,
    uuid                    DATA_TYPE_VARCHAR  NOT NULL,
    user                    DATA_TYPE_VARCHAR  NOT NULL,
    ip                      DATA_TYPE_VARCHAR  NOT NULL,
    reason                  TEXT               NOT NULL,
    server                  DATA_TYPE_VARCHAR  NOT NULL,
    date                    DATA_TYPE_DATETIME NOT NULL,
    active                  DATA_TYPE_BOOLEAN  NOT NULL,
    executed_by             DATA_TYPE_VARCHAR  NOT NULL,
    duration                DATA_TYPE_BIGINT   NOT NULL,
    type                    DATA_TYPE_VARCHAR  NOT NULL,
    removed                 DATA_TYPE_BOOLEAN  NOT NULL DEFAULT 0,
    removed_by              DATA_TYPE_VARCHAR,
    removed_at              DATA_TYPE_DATETIME NULL DEFAULT NULL,
    punishmentaction_status DATA_TYPE_BOOLEAN  NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_bans_e ON {bans-table} (executed_by);

CREATE INDEX idx_bans_ut ON {bans-table} (uuid, type);
CREATE INDEX idx_bans_uat ON {bans-table} (uuid, active, type);
CREATE INDEX idx_bans_uast ON {bans-table} (uuid, active, server, type);

CREATE INDEX idx_bans_it ON {bans-table} (ip, type);
CREATE INDEX idx_bans_iat ON {bans-table} (ip, active, type);
CREATE INDEX idx_bans_iast ON {bans-table} (ip, active, server, type);

CREATE TABLE IF NOT EXISTS {mutes-table}
(
    id                      DATA_TYPE_SERIAL,
    uuid                    DATA_TYPE_VARCHAR  NOT NULL,
    user                    DATA_TYPE_VARCHAR  NOT NULL,
    ip                      DATA_TYPE_VARCHAR  NOT NULL,
    reason                  TEXT               NOT NULL,
    server                  DATA_TYPE_VARCHAR  NOT NULL,
    date                    DATA_TYPE_DATETIME NOT NULL,
    active                  DATA_TYPE_BOOLEAN  NOT NULL,
    executed_by             DATA_TYPE_VARCHAR  NOT NULL,
    duration                DATA_TYPE_BIGINT   NOT NULL,
    type                    DATA_TYPE_VARCHAR  NOT NULL,
    removed                 DATA_TYPE_BOOLEAN  NOT NULL DEFAULT 0,
    removed_by              DATA_TYPE_VARCHAR,
    removed_at              DATA_TYPE_DATETIME NULL DEFAULT NULL,
    punishmentaction_status DATA_TYPE_BOOLEAN  NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_mutes_e ON {mutes-table} (executed_by);

CREATE INDEX idx_mutes_ut ON {mutes-table} (uuid, type);
CREATE INDEX idx_mutes_uat ON {mutes-table} (uuid, active, type);
CREATE INDEX idx_mutes_uast ON {mutes-table} (uuid, active, server, type);

CREATE INDEX idx_mutes_it ON {mutes-table} (ip, type);
CREATE INDEX idx_mutes_iat ON {mutes-table} (ip, active, type);
CREATE INDEX idx_mutes_iast ON {mutes-table} (ip, active, server, type);

CREATE TABLE IF NOT EXISTS {kicks-table}
(
    id                      DATA_TYPE_SERIAL,
    uuid                    DATA_TYPE_VARCHAR  NOT NULL,
    user                    DATA_TYPE_VARCHAR  NOT NULL,
    ip                      DATA_TYPE_VARCHAR  NOT NULL,
    reason                  TEXT               NOT NULL,
    server                  DATA_TYPE_VARCHAR  NOT NULL,
    date                    DATA_TYPE_DATETIME NOT NULL,
    executed_by             DATA_TYPE_VARCHAR  NOT NULL,
    punishmentaction_status DATA_TYPE_BOOLEAN  NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_kicks_u ON {kicks-table} (uuid);
CREATE INDEX idx_kicks_e ON {kicks-table} (executed_by);

CREATE TABLE IF NOT EXISTS {warns-table}
(
    id                      DATA_TYPE_SERIAL,
    uuid                    DATA_TYPE_VARCHAR  NOT NULL,
    user                    DATA_TYPE_VARCHAR  NOT NULL,
    ip                      DATA_TYPE_VARCHAR  NOT NULL,
    reason                  TEXT               NOT NULL,
    server                  DATA_TYPE_VARCHAR  NOT NULL,
    date                    DATA_TYPE_DATETIME NOT NULL,
    executed_by             DATA_TYPE_VARCHAR  NOT NULL,
    punishmentaction_status DATA_TYPE_BOOLEAN  NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_warns_u ON {warns-table} (uuid);
CREATE INDEX idx_warns_e ON {warns-table} (executed_by);

CREATE TABLE IF NOT EXISTS {punishmentactions-table}
(
    id       DATA_TYPE_SERIAL,
    uuid     DATA_TYPE_VARCHAR  NOT NULL,
    user     DATA_TYPE_VARCHAR  NOT NULL,
    ip       DATA_TYPE_VARCHAR  NOT NULL,
    actionid DATA_TYPE_VARCHAR  NOT NULL,
    date     DATA_TYPE_DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS {reports-table}
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

CREATE INDEX idx_reports_u ON {reports-table} (uuid);
CREATE INDEX idx_reports_ud ON {reports-table} (uuid, date);
CREATE INDEX idx_reports_urb ON {reports-table} (uuid, reported_by);

CREATE TABLE IF NOT EXISTS {messagequeue-table}
(
    id      DATA_TYPE_SERIAL,
    user    DATA_TYPE_VARCHAR  NOT NULL,
    message TEXT               NOT NULL,
    date    DATA_TYPE_DATETIME NOT NULL,
    type    DATA_TYPE_VARCHAR  NOT NULL,
    active  DATA_TYPE_BOOLEAN  NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_messagequeue_u ON {messagequeue-table} (user, type);