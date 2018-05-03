package com.dbsoftwares.bungeeutilisals.api.language;

/*
 * Created by DBSoftwares on 01/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import java.util.UUID;

public interface LanguageIntegration {

    /**
     * Gets the language for the given UUID.
     *
     * @param uuid UUID you want the language from.
     * @return Language of this UUID, default if not found.
     */
    Language getLanguage(UUID uuid);

}