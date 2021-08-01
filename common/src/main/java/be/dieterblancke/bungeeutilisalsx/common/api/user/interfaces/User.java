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

package be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSettings;
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.language.LanguageConfig;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.MessageQueue;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserCooldowns;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Version;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.QueuedMessage;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public interface User extends MessageRecipient
{

    /**
     * Loads the user.
     *
     * @param uuid The parent player uuid, null if console
     */
    void load( UUID uuid );

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
    String getIp();

    /**
     * @return The language of the User.
     */
    Language getLanguage();

    /**
     * Sets the language of the User.
     *
     * @param language The new language.
     */
    void setLanguage( Language language );

    /**
     * Sends a raw message to the User, without BungeeUtilisalsX prefix.
     *
     * @param message The message which has to be sent.
     */
    void sendRawMessage( String message );

    /**
     * Sends a raw message to the User, without BungeeUtilisalsX prefix, but with colors replaced.
     *
     * @param message The message which has to be sent, will be colored.
     */
    void sendRawColorMessage( String message );

    /**
     * Sends a message to the User with the BungeeUtilisalsX prefix + colors will be replaced.
     *
     * @param message The message which has to be sent. The BungeeUtilisalsX prefix will appear before.
     */
    default void sendMessage( String message )
    {
        if ( message.isEmpty() )
        {
            return;
        }
        sendMessage( getLanguageConfig().getConfig().getString( "prefix" ), PlaceHolderAPI.formatMessage( this, message ) );
    }

    /**
     * Searches a message in the user's language configuration and sends that message.
     *
     * @param path The path to the message in the language file.
     */
    default void sendLangMessage( String path )
    {
        this.getLanguageConfig().sendLangMessage( this, path );
    }

    /**
     * Searches a message in the user's language configuration and sends that message.
     * If the formatter functions are given, they can be used to manipulate the string.
     *
     * @param path                     The path to the message in the language file.
     * @param prefix                   Should a prefix be added in front of the message or not?
     * @param prePlaceholderFormatter  This string formatter can be used to manipulate the string before placeholder replacement.
     * @param postPlaceholderFormatter This string formatter can be used to manipulate the string after placeholder replacement.
     * @param placeholders             The placeholders and their values (placeholder on odd place, value on even place behind placeholder)
     */
    default void sendLangMessage( String path, boolean prefix, Function<String, String> prePlaceholderFormatter, Function<String, String> postPlaceholderFormatter, Object... placeholders )
    {
        this.getLanguageConfig().sendLangMessage( this, path, prefix, prePlaceholderFormatter, postPlaceholderFormatter, placeholders );
    }

    /**
     * Searches a message in the user's language configuration and sends that message formatted with placeholders.
     *
     * @param path         The path to the message in the language file.
     * @param placeholders The placeholders and their values (placeholder on odd place, value on even place behind placeholder)
     */
    default void sendLangMessage( String path, Object... placeholders )
    {
        this.getLanguageConfig().sendLangMessage( this, true, path, placeholders );
    }

    /**
     * Searches a message in the user's language configuration and sends that message.
     *
     * @param prefix Should a prefix be added in front of the message or not?
     * @param path   The path to the message in the language file.
     */
    default void sendLangMessage( boolean prefix, String path )
    {
        this.getLanguageConfig().sendLangMessage( this, prefix, path, new Object[0] );
    }

    /**
     * Searches a message in the user's language configuration and sends that message formatted with placeholders.
     *
     * @param prefix       Should a prefix be added in front of the message or not?
     * @param path         The path to the message in the language file.
     * @param placeholders The placeholders and their values (placeholder on odd place, value on even place behind placeholder)
     */
    default void sendLangMessage( boolean prefix, String path, Object... placeholders )
    {
        this.getLanguageConfig().sendLangMessage( this, path, prefix, null, null, placeholders );
    }

    /**
     * Sends a message to the User with the given prefix + colors will be replaced.
     *
     * @param prefix  The prefix for the message. Mostly used for plugin prefixes.
     * @param message The message which has to be sent.
     */
    default void sendMessage( String prefix, String message )
    {
        sendMessage( Utils.format( prefix + message ) );
    }

    /**
     * Sends a BaseComponent message to the user, colors will be formatted.
     *
     * @param component The component to be sent.
     */
    void sendMessage( BaseComponent component );

    /**
     * Sends a BaseComponent message to the user, colors will be formatted.
     *
     * @param component The component to be sent.
     */
    void sendMessage( BaseComponent[] component );

    /**
     * Synchronously kicks the User with a certain reason.
     *
     * @param reason The reason of the kick.
     */
    void kick( String reason );

    /**
     * Kicks user with message from language file.
     *
     * @param path         Path in language file.
     * @param placeholders Placeholders to be replaced.
     */
    void langKick( String path, Object... placeholders );

    /**
     * Kicks the User with a certain reason.
     *
     * @param reason The reason of the kick.
     */
    void forceKick( String reason );

    /**
     * @return The user's name.
     */
    String getName();

    /**
     * @return The user's uuid.
     */
    UUID getUuid();

    /**
     * Sends the standard no permission message to the User.
     */
    void sendNoPermMessage();

    /**
     * @return Returns if the User is in Socialspy mode or not.
     */
    boolean isSocialSpy();

    /**
     * Sets the Socialspy of the User on or off.
     *
     * @param socialspy The status of the Socialspy, true for on, false for off.
     */
    void setSocialSpy( boolean socialspy );

    /**
     * @return Returns if the User is in CommandSpy mode or not.
     */
    boolean isCommandSpy();

    /**
     * Sets the CommandSpy of the User on or off.
     *
     * @param commandspy The status of the CommandSpy, true for on, false for off.
     */
    void setCommandSpy( boolean commandspy );

    /**
     * @return The ping of the current user.
     */
    int getPing();

    /**
     * @return The user his language config.
     */
    default LanguageConfig getLanguageConfig()
    {
        return this.getLanguageConfig( BuX.getInstance().getName() );
    }

    /**
     * @return The user his language config.
     */
    default LanguageConfig getLanguageConfig( final String plugin )
    {
        return BuX.getApi().getLanguageManager().getLanguageConfiguration( plugin, this );
    }

    /**
     * @return True if console, false if player.
     */
    boolean isConsole();

    /**
     * @return the name of the server the user is in, returns BUNGEE in case of Console.
     */
    String getServerName();

    /**
     * @return true if staffchat is enabled, false if not.
     */
    boolean isInStaffChat();

    /**
     * Changes staffchat state.
     *
     * @param staffchat true if staffchat should be enabled, false if it should be disabled.
     */
    void setInStaffChat( boolean staffchat );

    /**
     * Sends the user to a server.
     *
     * @param proxyServer the server to send the user to.
     */
    void sendToServer( IProxyServer proxyServer );

    /**
     * @return the version the user is playing on
     */
    Version getVersion();

    /**
     * @return a list of the user's Friends
     */
    List<FriendData> getFriends();

    /**
     * @return the used friend settings
     */
    FriendSettings getFriendSettings();

    /**
     * @param permission The permission to check
     * @return This simply calls the parent.hasPermission() method
     */
    boolean hasPermission( String permission );

    /**
     * @param permission The permission to check
     * @param specific   if true, only the permission specifically will be checked, if false, permissions like "*" and "bungeeutilisalsx.*" will be checked too.
     * @return This simply calls the parent.hasPermission() method
     */
    boolean hasPermission( String permission, boolean specific );

    /**
     * @param permissions The permissions to check
     * @return This simply calls the parent.hasPermission() method
     */
    boolean hasAnyPermission( String... permissions );

    /**
     * @return the list of queued messages for this user.
     */
    MessageQueue<QueuedMessage> getMessageQueue();

    /**
     * Executes the queued message
     */
    void executeMessageQueue();

    /**
     * This method makes the user execute a given command.
     *
     * @param command the command to be executed.
     */
    void executeCommand( String command );

    /**
     * Sends an action bar to the user with a given message.
     *
     * @param actionbar the message to be displayed in the action bar.
     */
    void sendActionBar( String actionbar );

    /**
     * Sends a title to the user.
     *
     * @param title    the title to be sent
     * @param subtitle the subtitle to be sent
     * @param fadein   the time the title should fade in
     * @param stay     the time the title should stay
     * @param fadeout  the time the title should fade out
     */
    void sendTitle( String title, String subtitle, int fadein, int stay, int fadeout );

    /**
     * @return true if user is not receiving pms, false if the user does allow pms
     */
    boolean isMsgToggled();

    /**
     * Changes the msgtoggle status.
     *
     * @param status true if toggled, false if not
     */
    void setMsgToggled( boolean status );

    /**
     * Sends a packet to this user.
     *
     * @param packet the packet to be sent.
     */
    void sendPacket( final Object packet );

    /**
     * Sets the tab header and footer for this user.
     *
     * @param header the header to be set.
     * @param footer the footer to be set.
     */
    void setTabHeader( final BaseComponent[] header, final BaseComponent[] footer );

    /**
     * @return the host the user joined with.
     */
    String getJoinedHost();

    /**
     * @return true if user is vanished, false if not
     */
    boolean isVanished();
}