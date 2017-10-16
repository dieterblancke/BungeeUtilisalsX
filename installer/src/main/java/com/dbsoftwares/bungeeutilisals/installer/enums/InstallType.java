package com.dbsoftwares.bungeeutilisals.installer.enums;

/*
 * Created by DBSoftwares on 14 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public enum InstallType {

    CORE("jars/BungeeUtilisals.jar", "This is the BungeeUtilisals Core jar.\nThe plugin you must build in order to put it into your BungeeCord server."),
    API("jars/BUtilisals API.jar", "This is the BungeeUtilisals API jar.\nThis jar is ment for Developers, it is integrated in the core jar too."),
    PARTY("jars/BUtilisalsParties.jar", "This is the BungeeUtilisals party jar.\nAn external party plugin which uses & REQUIRES the Core jar to run."),
    FRIENDS("jars/BUtilisalsFriends.jar", "This is the BungeeUtilisals friend jar.\nAn external friend plugin which uses & REQUIRES the Core jar to run."),
    PUNISHMENTS("jars/BUtilisalsPunishments.jar", "This is the BungeeUtilisals punishments jar.\nAn external punishment plugin which uses & REQUIRES the Core jar to run."),
    PUNISHMENT_SITE("site/Punishment Site.zip", "This is the web addon for the BungeeUtilisals punishment system.");

    String jar;
    String description;

    InstallType(String jar, String description) {
        this.jar = jar;
        this.description = description;
    }

    public String getJarName() {
        return jar;
    }

    public String getFolder() {
        String[] array = jar.split("/");

        return array[0];
    }

    public String getJar() {
        String[] array = jar.split("/");

        return array[array.length - 1];
    }

    public String getDescription() {
        return description;
    }
}