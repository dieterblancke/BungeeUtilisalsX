package com.dbsoftwares.bungeeutilisals.api.settings;

public enum SettingType {

    // General Setting types
    MYSQL("mysql.yml"),

    // Chat Setting types
    ANTISWEAR("chat/antiswear.yml"),
    ANTIAD("chat/antiad.yml"),
    ANTICAPS("chat/anticaps.yml"),
    ANTISPAM("chat/antispam.yml"),
    UTFSYMBOLS("chat/utfsymbols.yml");

    String path;

    SettingType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}