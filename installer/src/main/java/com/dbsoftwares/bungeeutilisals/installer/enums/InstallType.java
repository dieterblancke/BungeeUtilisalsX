package com.dbsoftwares.bungeeutilisals.installer.enums;

/*
 * Created by DBSoftwares on 14 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public enum InstallType {

    CORE("BungeeUtilisals.jar"), PARTY("BUtilisalsParties.jar"), FRIENDS("BUtilisalsFriends.jar");

    String jar;

    InstallType(String jar) {
        this.jar = jar;
    }

    public String getJarName() {
        return jar;
    }
}