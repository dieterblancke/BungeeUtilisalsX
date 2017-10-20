package com.dbsoftwares.bungeeutilisals.api.language;

/*
 * Created by DBSoftwares on 15 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.user.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import java.io.File;
import java.util.Optional;

public interface ILanguageManager {

    /**
     * @return The default language, if not found, it will return the first available language, if not found, it will return an empty optional.
     */
    Optional<Language> getDefaultLanguage();

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
     */
    void addPlugin(Plugin plugin, File folder);

    /**
     * Loads default language files of a plugin.
     * @param plugin The plugin of which the languages should be loaded.
     */
    void loadLanguages(Plugin plugin);

    /**
     * @param plugin The plugin of which you want to get the language config.
     * @param user The user of which you want to get the config.
     * @return The Configuration bound to the User's language.
     */
    Configuration getLanguageConfiguration(Plugin plugin, User user);

    /**
     * @param plugin The plugin of which you want to get the language config.
     * @param player The player of which you want to get the config.
     * @return The Configuration bound to the Player's language.
     */
    Configuration getLanguageConfiguration(Plugin plugin, ProxiedPlayer player);

    /**
     * @param plugin The plugin of which you want to get the language config.
     * @param sender The CommandSender of which you want to get the config.
     * @return The Configuration bound to the CommandSender's language, default language config if language isn't set.
     */
    Configuration getLanguageConfiguration(Plugin plugin, CommandSender sender);

    /**
     * @param plugin The plugin of which you want to get the File.
     * @param language The language of which you want to get the file.
     * @return The file containing language settings for a certain plugin.
     */
    File getFile(Plugin plugin, Language language);

    /**
     * @param plugin The plugin of which you want to get the Configuration
     * @param language The language of which you want to get the Configuration.
     * @return The Configuration bound to the certain plugin and language.
     */
    Configuration getConfig(Plugin plugin, Language language);

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