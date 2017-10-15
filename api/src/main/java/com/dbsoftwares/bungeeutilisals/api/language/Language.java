package com.dbsoftwares.bungeeutilisals.api.language;

/*
 * Created by DBSoftwares on 15 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */
public enum Language {

    ENGLISH, DUTCH, SPANISH, ARABIC;

    /**
     * @return The language name in lowercase.
     */
    public String getName() {
        return toString().toLowerCase();
    }

    /**
     * @return The file name of a Language.
     */
    public String getFileName() {
        return toString().toLowerCase() + ".yml";
    }
}