package com.dbsoftwares.bungeeutilisals.api.user.interfaces;

import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.user.UserCooldowns;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public interface User {

    /**
     * Loads the user in RAM.
     * @param name The user's name.
     * @param uuid The user's UUID.
     * @param IP The user's IP.
     */
    void load(String name, UUID uuid, String IP);

    /**
     * Unloads the User from storage.
     */
    void unload();

    /**
     * Saves the local user data onto the database.
     */
    void save();

    /**
     * @return User data is being stored in here.
     */
    UserStorage getStorage();

    /**
     * @return A small and simple user cooldown utility.
     */
    UserCooldowns getCooldowns();

    /**
     * @return The IP of the User.
     */
    String getIP();

    /**
     * @return The language of the User.
     */
    Language getLanguage();

    /**
     * Sets the language of the User.
     *
     * @param language The new language.
     */
    void setLanguage(Language language);

    /**
     * @return The User casted to CommandSender.
     */
    CommandSender sender();

    /**
     * Sends a raw message to the User, without CentrixCore prefix.
     *
     * @param message The message which has to be sent.
     */
    void sendRawMessage(String message);

    /**
     * Sends a raw message to the User, without CentrixCore prefix, but with colors replaced.
     *
     * @param message The message which has to be sent, will be colored.
     */
    void sendRawColorMessage(String message);

    /**
     * Sends a message to the User with the CentrixCore prefix + colors will be replaced.
     *
     * @param message The message which has to be sent. The CentrixCore prefix will appear before.
     */
    void sendMessage(String message);

    /**
     * Searches a message in the user's language configuration and sends that message.
     *
     * @param path The path to the message in the language file.
     */
    void sendLangMessage(String path);

    /**
     * Searches a message in the user's language configuration and sends that message formatted with placeholders.
     *
     * @param path         The path to the message in the language file.
     * @param placeholders The placeholders and their values (placeholder on odd place, value on even place behind placeholder)
     */
    void sendLangMessage(String path, Object... placeholders);

    /**
     * Sends a message to the User with the given prefix + colors will be replaced.
     *
     * @param prefix  The prefix for the message. Mostly used for plugin prefixes.
     * @param message The message which has to be sent.
     */
    void sendMessage(String prefix, String message);

    /**
     * Sends a BaseComponent message to the user, colors will be formatted.
     * @param component The component to be sent.
     */
    void sendMessage(BaseComponent component);

    /**
     * Sends a BaseComponent message to the user, colors will be formatted.
     * @param components The components to be sent.
     */
    void sendMessage(BaseComponent[] components);

    /**
     * Synchronously kicks the User with a certain reason.
     *
     * @param reason The reason of the kick.
     */
    void kick(String reason);

    /**
     * Kicks user with message from language file.
     *
     * @param path         Path in language file.
     * @param placeholders Placeholders to be replaced.
     */
    void langKick(String path, Object... placeholders);

    /**
     * Kicks the User with a certain reason.
     *
     * @param reason The reason of the kick.
     */
    void forceKick(String reason);

    /**
     * @return The user's name.
     */
    String getName();

    /**
     * @return The user's UUID.
     */
    UUID getUUID();

    /**
     * Sends the standard no permission message to the User.
     */
    void sendNoPermMessage();

    /**
     * Sets the Socialspy of the User on or off.
     *
     * @param socialspy The status of the Socialspy, true for on, false for off.
     */
    void setSocialspy(Boolean socialspy);

    /**
     * @return Returns if the User is in Socialspy mode or not.
     */
    Boolean isSocialSpy();

    /**
     * @return The Player who's behind the User.
     */
    ProxiedPlayer getParent();

    /**
     * @return The user his language config.
     */
    IConfiguration getLanguageConfig();

    /**
     * Returns an ExperimentalUser object (containing functions which are unstable or in development).
     * @return An ExperimentalUser Object.
     */
    IExperimentalUser experimental();

    /**
     * @return True if console, false if player.
     */
    boolean isConsole();

    /**
     * @return the name of the server the user is in, returns BUNGEE in case of Console.
     */
    String getServerName();

    /**
     * @return true if user is muted, false if not.
     */
    boolean isMuted();

    /**
     * @return PunishmentInfo of current mute (if muted), null if not muted.
     */
    PunishmentInfo getMuteInfo();

    /**
     * Sets the user mute data in local cache.
     *
     * @param info The info to be stored.
     */
    void setMute(PunishmentInfo info);
}