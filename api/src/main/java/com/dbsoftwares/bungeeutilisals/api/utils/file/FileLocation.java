package com.dbsoftwares.bungeeutilisals.api.utils.file;

import lombok.Getter;

public enum FileLocation {

    CONFIG("config.yml"),
    MYSQL("mysql.yml"),
    ANTISWEAR("chat/antiswear.yml"),
    ANTICAPS("chat/anticaps.yml"),
    ANTIAD("chat/antiadvertise.yml"),
    ANTISPAM("chat/antispam.yml"),
    UTFSYMBOLS("chat/utfsymbols.yml"),
    FRIENDS_CONFIG("friends/config.yml"),
    PUNISHMENTS_CONFIG("punishments/config.yml"),
    LANGUAGES_CONFIG("languages/config.yml");

    @Getter String path;

    FileLocation(String path) {
        this.path = path;
    }
}