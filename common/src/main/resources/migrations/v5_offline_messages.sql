drop table bu_messagequeue;

CREATE TABLE IF NOT EXISTS bu_offline_message
(
    id          DATA_TYPE_SERIAL    NOT NULL,
    username    DATA_TYPE_VARCHAR   NOT NULL,
    message     DATA_TYPE_LONGTEXT  NOT NULL,
    parameters  DATA_TYPE_LONGTEXT  NOT NULL,
    active      DATA_TYPE_BOOLEAN   NOT NULL,
    PRIMARY KEY (id)
);