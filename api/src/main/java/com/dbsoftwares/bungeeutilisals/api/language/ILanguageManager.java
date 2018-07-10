package com.dbsoftwares.bungeeutilisals.api.language;

/*
 * Created by DBSoftwares on 15 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileStorageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.Optional;

public interface ILanguageManager {

    /**
     * @return The language found with the given name, if not found: default language.
     */
    Language getLangOrDefault(String language);

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
    void setLanguageIntegration(LanguageIntegration integration);

    /**
     * @return The default language, if not found, it will return the first available language, if still not present, null will be returned.
     */
    Language getDefaultLanguage();

    /**
     * Method to retrieve a Language by name.
     * @param language The name of the language.
     * @return An optional containing the language IF present.
     */
    Optional<Language> getLanguage(String language);

    /**
     * Registers a plugin into the ILanguageManager
     * @param plugin The plugin you want to register.
     * @param folder The folder in which the languages will be located.
     * @param type The FileStorageType which will be used, Json or Yaml.
     */
    void addPlugin(Plugin plugin, File folder, FileStorageType type);

    /**
     * Loads default language files of a plugin.
     * @param plugin The plugin of which the languages should be loaded.
     */
    void loadLanguages(Plugin plugin);

    /**
     * @param plugin The plugin of which you want to get the language config.
     * @param user The user of which you want to get the config.
     * @return The JsonConfiguration bound to the User's language.
     */
    IConfiguration getLanguageConfiguration(Plugin plugin, User user);

    /**
     * @param plugin The plugin of which you want to get the language config.
     * @param player The player of which you want to get the config.
     * @return The JsonConfiguration bound to the Player's language.
     */
    IConfiguration getLanguageConfiguration(Plugin plugin, ProxiedPlayer player);

    /**
     * @param plugin The plugin of which you want to get the language config.
     * @param sender The CommandSender of which you want to get the config.
     * @return The JsonConfiguration bound to the CommandSender's language, default language config if language isn't set.
     */
    IConfiguration getLanguageConfiguration(Plugin plugin, CommandSender sender);

    /**
     * @param plugin The plugin of which you want to get the File.
     * @param language The language of which you want to get the file.
     * @return The file containing language settings for a certain plugin.
     */
    File getFile(Plugin plugin, Language language);

    /**
     * @param plugin The plugin of which you want to get the JsonConfiguration
     * @param language The language of which you want to get the JsonConfiguration.
     * @return The JsonConfiguration bound to the certain plugin and language.
     */
    IConfiguration getConfig(Plugin plugin, Language language);

    /**
     * @param plugin The plugin you want to check.
     * @param language The language you want to check.
     * @return True if the certain plugin and language is registered, false if not.
     */
    Boolean isRegistered(Plugin plugin, Language language);

    /**
     * @param plugin The plugin of which you want to save the language.
     * @param language The language you want to save.
     * @return True if successful, false if not.
     */
    Boolean saveLanguage(Plugin plugin, Language language);

    /**
     *
     * @param plugin The plugin of which you want to reload the language config.
     * @param language The language config you want to reload.
     * @return True if successful, false if not.
     */
    Boolean reloadConfig(Plugin plugin, Language language);
}