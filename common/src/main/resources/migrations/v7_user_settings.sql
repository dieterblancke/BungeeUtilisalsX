CREATE TABLE IF NOT EXISTS bu_user_settings
(
    id            DATA_TYPE_SERIAL    NOT NULL,
    uuid          DATA_TYPE_VARCHAR   NOT NULL,
    setting_type  DATA_TYPE_VARCHAR   NOT NULL,
    setting_value DATA_TYPE_LONGTEXT  NOT NULL,
    PRIMARY KEY (id)
);