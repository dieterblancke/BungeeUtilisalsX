alter table bu_friendsettings rename to bu_friendsettings_old;

CREATE TABLE IF NOT EXISTS bu_friendsettings
(
    `user`  DATA_TYPE_VARCHAR NOT NULL,
    setting DATA_TYPE_VARCHAR NOT NULL,
    `value` TEXT NOT NULL,
    PRIMARY KEY (`user`)
);