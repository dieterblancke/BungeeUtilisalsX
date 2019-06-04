/* Converted using SQLines.com/online */

CREATE SEQUENCE {users-table}_seq;

CREATE TABLE IF NOT EXISTS {users-table}
(
  id         INT DEFAULT NEXTVAL ('{users-table}_seq') NOT NULL,
  uuid       VARCHAR(48) UNIQUE     NOT NULL,
  username   VARCHAR(32)            NOT NULL,
  ip         VARCHAR(32)            NOT NULL,
  language   VARCHAR(24)            NOT NULL,
  firstlogin TIMESTAMP(0)               NOT NULL,
  lastlogout TIMESTAMP(0)               NOT NULL,
  PRIMARY KEY (id)
);

ALTER SEQUENCE {users-table}_seq RESTART WITH 1;

CREATE INDEX idx_users ON {users-table} (id, uuid, username, ip);

CREATE TABLE IF NOT EXISTS {ignoredusers-table}
(
  user      INT NOT NULL,
  ignored   INT NOT NULL,
  PRIMARY KEY(user, ignored),
  FOREIGN KEY (user) REFERENCES {users-table}(id),
  FOREIGN KEY (ignored) REFERENCES {users-table}(id)
);

ALTER SEQUENCE {ignoredusers-table}_seq RESTART WITH 1;

CREATE TABLE IF NOT EXISTS {friendsettings-table}
(
  user        VARCHAR(48) NOT NULL,
  requests    SMALLINT  NOT NULL,
  messages    SMALLINT  NOT NULL,
  PRIMARY KEY (user)
);

ALTER SEQUENCE {friendsettings-table}_seq RESTART WITH 1;

CREATE INDEX idx_friendset ON {friendsettings-table} (user, requests, messages);

CREATE TABLE IF NOT EXISTS {friends-table}
(
  user    VARCHAR(48)                        NOT NULL,
  friend  VARCHAR(48)                        NOT NULL,
  created TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (user, friend)
);

ALTER SEQUENCE {friends-table}_seq RESTART WITH 1;

CREATE INDEX idx_friends ON {friends-table} (user, friend);

CREATE TABLE IF NOT EXISTS {friendrequests-table}
(
  user         VARCHAR(48)                        NOT NULL,
  friend       VARCHAR(48)                        NOT NULL,
  requested_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (user, friend)
);

ALTER SEQUENCE {friendrequests-table}_seq RESTART WITH 1;

CREATE INDEX idx_friendreq ON {friendrequests-table} (user, friend);

CREATE SEQUENCE {bans-table}_seq;

CREATE TABLE IF NOT EXISTS {bans-table}
(
  id          INT DEFAULT NEXTVAL ('{bans-table}_seq') NOT NULL,
  uuid        VARCHAR(48)            NOT NULL,
  user        VARCHAR(32)            NOT NULL,
  ip          VARCHAR(32)            NOT NULL,
  reason      TEXT                   NOT NULL,
  server      VARCHAR(32)            NOT NULL,
  date        TIMESTAMP(0)               NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active      SMALLINT             NOT NULL,
  executed_by VARCHAR(64)            NOT NULL,
  duration    TEXT                   NOT NULL,
  type        VARCHAR(16)            NOT NULL,
  removed     SMALLINT             NOT NULL DEFAULT 0,
  removed_by  VARCHAR(32),
  PRIMARY KEY (id),
  FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

ALTER SEQUENCE {bans-table}_seq RESTART WITH 1;

CREATE INDEX idx_bans ON {bans-table} (id, uuid, user, ip, active, server);

CREATE SEQUENCE {mutes-table}_seq;

CREATE TABLE IF NOT EXISTS {mutes-table}
(
  id          INT DEFAULT NEXTVAL ('{mutes-table}_seq') NOT NULL,
  uuid        VARCHAR(48)            NOT NULL,
  user        VARCHAR(32)            NOT NULL,
  ip          VARCHAR(32)            NOT NULL,
  reason      TEXT                   NOT NULL,
  server      VARCHAR(32)            NOT NULL,
  date        TIMESTAMP(0)               NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active      SMALLINT             NOT NULL,
  executed_by VARCHAR(64)            NOT NULL,
  duration    TEXT                   NOT NULL,
  type        VARCHAR(16)            NOT NULL,
  removed     SMALLINT             NOT NULL DEFAULT 0,
  removed_by  VARCHAR(32),
  PRIMARY KEY (id),
  FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

ALTER SEQUENCE {mutes-table}_seq RESTART WITH 1;

CREATE INDEX idx_mutes ON {mutes-table} (id, uuid, user, ip, active, server);

CREATE SEQUENCE {kicks-table}_seq;

CREATE TABLE IF NOT EXISTS {kicks-table}
(
  id          INT DEFAULT NEXTVAL ('{kicks-table}_seq')             NOT NULL,
  uuid        VARCHAR(36)                        NOT NULL,
  user        VARCHAR(32)                        NOT NULL,
  ip          VARCHAR(32)                        NOT NULL,
  reason      TEXT                               NOT NULL,
  server      VARCHAR(32)                        NOT NULL,
  date        TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  executed_by VARCHAR(64)                        NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

ALTER SEQUENCE {kicks-table}_seq RESTART WITH 1;

CREATE INDEX idx_kicks ON {kicks-table} (id, uuid, user, ip);

CREATE SEQUENCE {warns-table}_seq;

CREATE TABLE IF NOT EXISTS {warns-table}
(
  id          INT DEFAULT NEXTVAL ('{warns-table}_seq')             NOT NULL,
  uuid        VARCHAR(36)                        NOT NULL,
  user        VARCHAR(32)                        NOT NULL,
  ip          VARCHAR(32)                        NOT NULL,
  reason      TEXT                               NOT NULL,
  server      VARCHAR(32)                        NOT NULL,
  date        TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  executed_by VARCHAR(64)                        NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

ALTER SEQUENCE {warns-table}_seq RESTART WITH 1;

CREATE INDEX idx_warns ON {warns-table} (id, uuid, user, ip);

CREATE SEQUENCE {punishmentactions-table}_seq;

CREATE TABLE IF NOT EXISTS {punishmentactions-table}
(
  id          INT DEFAULT NEXTVAL ('{punishmentactions-table}_seq')             NOT NULL,
  uuid        VARCHAR(36)                        NOT NULL,
  user        VARCHAR(32)                        NOT NULL,
  ip          VARCHAR(32)                        NOT NULL,
  actionid    VARCHAR(36)                        NOT NULL,
  date        TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (uuid) REFERENCES {users-table} (uuid)
);

ALTER SEQUENCE {punishmentactions-table}_seq RESTART WITH 1;

CREATE INDEX idx_punishactions ON {punishmentactions-table} (id, uuid, user, ip, actionid);