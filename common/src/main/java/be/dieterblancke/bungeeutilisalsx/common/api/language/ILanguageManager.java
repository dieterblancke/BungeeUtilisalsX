/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package be.dieterblancke.bungeeutilisalsx.common.api.language;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.configuration.api.FileStorageType;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface ILanguageManager
{

    /**
     * @param language The name of the language to be searched.
     * @return The language found with the given name, if not found: default language.
     */
    Language getLangOrDefault( String language );

    /**
     * @return The registered language integration.
     */
    LanguageIntegration getLanguageIntegration();

    /**
     * Allows to make a custom User language integration.
     * This basically means that if you have a language system already, you can fetch the language from there and tell it to BungeeUtilisals.
     *
     * @param integration LanguageIntegration instance
     */
    void setLanguageIntegration( LanguageIntegration integration );

    /**
     * @return The default language, if not found, it will return the first available language, if still not present, null will be returned.
     */
    Language getDefaultLanguage();

    /**
     * Method to retrieve a Language by name.
     *
     * @param language The name of the language.
     * @return An optional containing the language IF present.
     */
    Optional<Language> getLanguage( String language );

    /**
     * Registers a plugin into the ILanguageManager
     *
     * @param plugin The plugin you want to register.
     * @param folder The folder in which the languages will be located.
     * @param type   The FileStorageType which will be used, Json or Yaml.
     */
    void addPlugin( String plugin, File folder, FileStorageType type );

    /**
     * Loads default language files of a plugin.
     *
     * @param plugin The plugin of which the languages should be loaded.
     */
    void loadLanguages( final Class<?> resourceClass, String plugin );

    /**
     * @param plugin The plugin of which you want to get the language config.
     * @param user   The user of which you want to get the config.
     * @return The JsonConfiguration bound to the User's language.
     */
    LanguageConfig getLanguageConfiguration( String plugin, User user );

    /**
     * @param plugin   The plugin of which you want to get the language config.
     * @param userName The user of which you want to get the config.
     * @return The JsonConfiguration bound to the User's language.
     */
    LanguageConfig getLanguageConfiguration( String plugin, String userName );

    /**
     * @param plugin   The plugin of which you want to get the File.
     * @param language The language of which you want to get the file.
     * @return The file containing language settings for a certain plugin.
     */
    File getFile( String plugin, Language language );

    /**
     * @param plugin   The plugin of which you want to get the JsonConfiguration
     * @param language The language of which you want to get the JsonConfiguration.
     * @return The JsonConfiguration bound to the certain plugin and language.
     */
    LanguageConfig getConfig( String plugin, Language language );

    /**
     * @param plugin   The plugin you want to check.
     * @param language The language you want to check.
     * @return True if the certain plugin and language is registered, false if not.
     */
    boolean isRegistered( String plugin, Language language );

    /**
     * @param plugin   The plugin of which you want to save the language.
     * @param language The language you want to save.
     * @return True if successful, false if not.
     */
    boolean saveLanguage( String plugin, Language language );

    /**
     * @param plugin   The plugin of which you want to reload the language config.
     * @param language The language config you want to reload.
     * @return True if successful, false if not.
     */
    boolean reloadConfig( String plugin, Language language );

    /**
     * @return A list of registered languages.
     */
    List<Language> getLanguages();
}