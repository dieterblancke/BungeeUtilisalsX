package com.dbsoftwares.bungeeutilisals.bungee.settings;

import lombok.Getter;

public enum FileLocations {

    CONFIG("config.yml"),
    MYSQL("mysql.yml"),
    ANTISWEAR("chat/antiswear.yml"),
    UTFSYMBOLS("chat/utfsymbols.yml"),
    FRIENDS_CONFIG("friends/config.yml"),
    PUNISHMENTS_CONFIG("punishments/config.yml");

    @Getter String path;

    FileLocations(String path) {
        this.path = path;
    }
}