CREATE TABLE IF NOT EXISTS `{users-table}` (
  id         INTEGER PRIMARY KEY AUTOINCREMENT      NOT NULL,
  uuid       VARCHAR(36) UNIQUE                     NOT NULL,
  username   VARCHAR(32)                            NOT NULL,
  ip         VARCHAR(32)                            NOT NULL,
  language   VARCHAR(24)                            NOT NULL,
  firstlogin DATETIME                               NOT NULL,
  lastlogout DATETIME                               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users
  ON `{users-table}` (uuid, username, ip);


CREATE TABLE IF NOT EXISTS `{friends-table}`
(
  user    VARCHAR(48)                        NOT NULL,
  friend  VARCHAR(48)                        NOT NULL,
  created DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (user, friend)
);

CREATE INDEX IF NOT EXISTS idx_friends
  ON `{friends-table}` (user, friend);


CREATE TABLE IF NOT EXISTS `{friendrequests-table}`
(
  user         VARCHAR(48)                        NOT NULL,
  friend       VARCHAR(48)                        NOT NULL,
  requested_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (user, friend)
);

CREATE INDEX IF NOT EXISTS idx_friendreq
  ON `{friends-table}` (user, friend);


CREATE TABLE IF NOT EXISTS `{friendsettings-table}`
(
  user     VARCHAR(48) NOT NULL,
  settings JSON        NOT NULL, /* TODO: change this to columns ... */
  PRIMARY KEY (user)
);


CREATE TABLE IF NOT EXISTS `{bans-table}`
(
  id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  uuid        VARCHAR(48) NOT NULL,
  user        VARCHAR(32) NOT NULL,
  ip          VARCHAR(32) NOT NULL,
  reason      TEXT        NOT NULL,
  server      VARCHAR(32) NOT NULL,
  date        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active      TINYINT(1)  NOT NULL,
  executed_by VARCHAR(64) NOT NULL,
  duration    LONG        NOT NULL,
  type        VARCHAR(16) NOT NULL,
  removed     TINYINT(1)  NOT NULL DEFAULT 0,
  removed_by  VARCHAR(32),
  FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
);

CREATE INDEX IF NOT EXISTS idx_bans
  ON `{bans-table}` (id, uuid, user, ip, active, server);


CREATE TABLE IF NOT EXISTS `{mutes-table}`
(
  id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  uuid        VARCHAR(48) NOT NULL,
  user        VARCHAR(32) NOT NULL,
  ip          VARCHAR(32) NOT NULL,
  reason      TEXT        NOT NULL,
  server      VARCHAR(32) NOT NULL,
  date        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  active      TINYINT(1)  NOT NULL,
  executed_by VARCHAR(64) NOT NULL,
  duration    LONG        NOT NULL,
  type        VARCHAR(16) NOT NULL,
  removed     TINYINT(1)  NOT NULL DEFAULT 0,
  removed_by  VARCHAR(32),
  FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
);

CREATE INDEX IF NOT EXISTS idx_mutes
  ON `{mutes-table}` (id, uuid, user, ip, active, server);


CREATE TABLE IF NOT EXISTS `{kicks-table}` (
  id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  uuid        VARCHAR(36)                       NOT NULL,
  user        VARCHAR(32)                       NOT NULL,
  ip          VARCHAR(32)                       NOT NULL,
  reason      TEXT                              NOT NULL,
  server      VARCHAR(32)                       NOT NULL,
  date        DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  executed_by VARCHAR(64)                       NOT NULL,
  FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
);

CREATE INDEX IF NOT EXISTS idx_kicks
  ON `{kicks-table}` (id, uuid, user, ip);


CREATE TABLE IF NOT EXISTS `{warns-table}` (
  id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  uuid        VARCHAR(36)                       NOT NULL,
  user        VARCHAR(32)                       NOT NULL,
  ip          VARCHAR(32)                       NOT NULL,
  reason      TEXT                              NOT NULL,
  server      VARCHAR(32)                       NOT NULL,
  date        DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  executed_by VARCHAR(64)                       NOT NULL,
  FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
);

CREATE INDEX IF NOT EXISTS idx_warns
  ON `{warns-table}` (id, uuid, user, ip);