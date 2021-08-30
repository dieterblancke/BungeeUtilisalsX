CREATE TABLE IF NOT EXISTS bu_api_keys
(
    id           DATA_TYPE_SERIAL    NOT NULL,
    api_key      DATA_TYPE_VARCHAR   NOT NULL,
    expire_date  DATA_TYPE_DATETIME  NOT NULL,
    permissions  DATA_TYPE_LONGTEXT  NOT NULL,
    PRIMARY KEY (id)
);