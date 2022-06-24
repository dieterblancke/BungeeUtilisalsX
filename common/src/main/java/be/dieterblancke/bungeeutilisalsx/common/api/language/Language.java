package be.dieterblancke.bungeeutilisalsx.common.api.language;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
public class Language
{

    public final String name;
    public final Boolean defaultLanguage;

    /**
     * @return The language name in lowercase.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return True if default language, false if not.
     */
    public Boolean isDefault()
    {
        return defaultLanguage;
    }

    @Override
    public String toString()
    {
        return name;
    }
}