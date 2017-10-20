package com.dbsoftwares.bungeeutilisals.api.language;

import lombok.RequiredArgsConstructor;

/*
 * Created by DBSoftwares on 15 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */
@RequiredArgsConstructor
public class Language {

    public final String name;
    public final Boolean defaultLanguage;

    /**
     * @return The language name in lowercase.
     */
    public String getName() {
        return name.toLowerCase();
    }

    /**
     * @return The file name of a Language.
     */
    public String getFileName() {
        return name + ".yml";
    }

    /**
     * @return True if default language, false if not.
     */
    public Boolean isDefault() {
        return defaultLanguage;
    }
}