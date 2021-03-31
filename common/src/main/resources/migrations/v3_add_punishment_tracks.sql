CREATE TABLE IF NOT EXISTS {punishmenttracks-table}
(
    id                      DATA_TYPE_SERIAL,
    uuid                    DATA_TYPE_VARCHAR  NOT NULL,
    track_id                DATA_TYPE_VARCHAR  NOT NULL,
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