package dev.endoy.bungeeutilisalsx.common.api.language;

import java.util.UUID;

public interface LanguageIntegration
{

    /**
     * Gets the language for the given uuid.
     *
     * @param uuid uuid you want the language from.
     * @return Language of this uuid, default if not found.
     */
    Language getLanguage( UUID uuid );

}