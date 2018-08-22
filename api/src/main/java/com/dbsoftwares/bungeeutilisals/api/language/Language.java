package com.dbsoftwares.bungeeutilisals.api.language;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/*
 * Created by DBSoftwares on 15 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

@EqualsAndHashCode
@RequiredArgsConstructor
public class Language {

    public final String name;
    public final Boolean defaultLanguage;

    /**
     * @return The language name in lowercase.
     */
    public String getName() {
        return name;
    }

    /**
     * @return True if default language, false if not.
     */
    public Boolean isDefault() {
        return defaultLanguage;
    }

    @Override
    public String toString() {
        return name;
    }
}