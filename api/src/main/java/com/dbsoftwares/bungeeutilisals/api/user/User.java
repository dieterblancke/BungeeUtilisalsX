package com.dbsoftwares.bungeeutilisals.api.user;

import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserPreLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.punishmentts.PunishmentInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public interface User {

    /**
     * Loads the user into storage.
     * @param event The even for the user to be loaded from.
     */
    void load(UserPreLoadEvent event);

    /**
     * Unloads the User from storage.
     */
    void unload();

    /**
     * Saves the local user data onto the database.
     */
    void save();

    /**
     * @return The Storage containing some personal User data ((name)tags, ignored players, ...)
     */
    UserStorage getStorage();

    /**
     * @return The ping (in ms) of an user.
     */
    Integer getPing();

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
     * @return If the User has been muted or not.
     */
    Boolean isMuted();

    /**
     * Sets the User his current mute.
     * @param info The data the mute should get.
     */
    void setCurrentMute(PunishmentInfo info);

    /**
     * @return Null if not muted, punishmentinfo if muted.
     */
    PunishmentInfo getCurrentMute();

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
     * Kicks the User with a certain reason.
     *
     * @param reason The reason of the kick.
     */
    void forceKick(String reason);

    /**
     * @return The User his name.
     */
    String getName();

    /**
     * Sends the standard no permission message to the User.
     */
    void sendNoPermMessage();

    /**
     * @return If the User is a staff or not (checked via permissions).
     */
    Boolean isStaff();

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
    Configuration getLanguageConfig();
}