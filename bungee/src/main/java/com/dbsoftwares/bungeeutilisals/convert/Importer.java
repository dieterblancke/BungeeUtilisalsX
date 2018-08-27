package com.dbsoftwares.bungeeutilisals.convert;

import lombok.Data;

import java.util.Map;

@Data
public abstract class Importer {

    private final ImporterType type;
    private final Map<String, String> properties;

    public Importer(final ImporterType type, final Map<String, String> properties) {
        this.type = type;
        this.properties = properties;
    }

    public ImporterType getType() {
        return type;
    }

    public abstract void startConverter();
}