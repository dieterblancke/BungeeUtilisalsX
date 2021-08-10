CREATE TABLE IF NOT EXISTS bu_punishmenttracks
(
    id                      DATA_TYPE_SERIAL,
    uuid                    DATA_TYPE_VARCHAR  NOT NULL,
    track_id                DATA_TYPE_VARCHAR  NOT NULL,
    server                  DATA_TYPE_VARCHAR  NOT NULL,
    executed_by             DATA_TYPE_VARCHAR  NOT NULL,
    date                    DATA_TYPE_DATETIME NOT NULL,
    active                  DATA_TYPE_BOOLEAN  NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_tracks_e ON bu_punishmenttracks (executed_by);

CREATE INDEX idx_tracks_u ON bu_punishmenttracks (uuid);
CREATE INDEX idx_tracks_ua ON bu_punishmenttracks (uuid, active);
CREATE INDEX idx_tracks_uas ON bu_punishmenttracks (uuid, active, server);