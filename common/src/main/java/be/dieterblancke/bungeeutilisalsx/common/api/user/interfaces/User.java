package be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.IBossBar;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSettings;
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.language.LanguageConfig;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao.OfflineMessage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserCooldowns;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserSettings;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.VersionsConfig.Version;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface User extends Messageable, HasLanguageConfig, HasMessagePlaceholders
{

    /**
     * Loads the user.
     *
     * @param player the player object
     */
    void load( Object player );

    /**
     * Unloads the User from storage.
     */
    void unload();

    /**
     * Saves the local user data onto the database.
     *
     * @param logout if the save action is executed on logout
     */
    void save( boolean logout );

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
    default void sendLangMessage( String path,
                                  boolean prefix,
                                  Function<String, String> prePlaceholderFormatter,
                                  Function<String, String> postPlaceholderFormatter,
                                  HasMessagePlaceholders placeholders )
    {
        this.getLanguageConfig().sendLangMessage( this, path, prefix, prePlaceholderFormatter, postPlaceholderFormatter, placeholders );
    }

    /**
     * Searches a message in the user's language configuration and sends that message formatted with placeholders.
     *
     * @param path         The path to the message in the language file.
     * @param placeholders The placeholders and their values (placeholder on odd place, value on even place behind placeholder)
     */
    default void sendLangMessage( String path, HasMessagePlaceholders placeholders )
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
        this.getLanguageConfig().sendLangMessage( this, prefix, path, MessagePlaceholders.empty() );
    }

    /**
     * Searches a message in the user's language configuration and sends that message formatted with placeholders.
     *
     * @param prefix       Should a prefix be added in front of the message or not?
     * @param path         The path to the message in the language file.
     * @param placeholders The placeholders and their values (placeholder on odd place, value on even place behind placeholder)
     */
    default void sendLangMessage( boolean prefix, String path, HasMessagePlaceholders placeholders )
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
    default void langKick( String path, HasMessagePlaceholders placeholders )
    {
        if ( getLanguageConfig().getConfig().isList( path ) )
        {
            final String reason = getLanguageConfig().getConfig().getStringList( path ).stream().map( str ->
                    placeholders.getMessagePlaceholders().format( str ) ).collect( Collectors.joining( "\n" ) );

            kick( reason );
        }
        else
        {
            String message = getLanguageConfig().getConfig().getString( path );
            message = placeholders.getMessagePlaceholders().format( message );
            kick( message );
        }
    }

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
    default LanguageConfig getLanguageConfig( String plugin )
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
     * Executes the queued message
     */
    default void sendOfflineMessages()
    {
        OfflineMessageDao offlineMessageDao = BuX.getApi().getStorageManager().getDao().getOfflineMessageDao();

        offlineMessageDao.getOfflineMessages( this.getName() ).thenAccept( messages ->
        {
            if ( !messages.isEmpty() )
            {
                this.sendLangMessage( "offlinemessages-join-header" );

                for ( OfflineMessage message : messages )
                {
                    this.sendLangMessage( message.getLanguagePath(), message.getPlaceholders() );
                    offlineMessageDao.deleteOfflineMessage( message.getId() );
                }
            }
        } );
    }

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
    default void sendActionBar( String actionbar )
    {
        asAudience().sendActionBar( Utils.format( this, actionbar ) );
    }

    /**
     * Sends a title to the user.
     *
     * @param title    the title to be sent
     * @param subtitle the subtitle to be sent
     * @param fadein   the time the title should fade in
     * @param stay     the time the title should stay
     * @param fadeout  the time the title should fade out
     */
    default void sendTitle( String title, String subtitle, int fadein, int stay, int fadeout )
    {
        asAudience().showTitle( Title.title(
                Utils.format( this, title ),
                Utils.format( this, subtitle ),
                Times.times( Duration.ofSeconds( fadein ), Duration.ofSeconds( stay ), Duration.ofSeconds( fadeout ) )
        ) );
    }

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
    void sendPacket( Object packet );

    /**
     * Sets the tab header and footer for this user.
     *
     * @param header the header to be set.
     * @param footer the footer to be set.
     */
    default void setTabHeader( Component header, Component footer )
    {
        asAudience().sendPlayerListHeaderAndFooter( header, footer );
    }

    /**
     * @return the host the user joined with.
     */
    String getJoinedHost();

    /**
     * @return true if user is vanished, false if not
     */
    boolean isVanished();

    /**
     * Sets the vanish state for a user
     *
     * @param vanished the vanish state to be set
     */
    void setVanished( boolean vanished );

    /**
     * @return the cached user group
     */
    String getGroup();

    /**
     * Gets the user current server
     *
     * @return an optional of the server the user is currently in
     */
    default Optional<IProxyServer> getCurrentServer()
    {
        return Optional.ofNullable( BuX.getInstance().proxyOperations().getServerInfo( this.getServerName() ) );
    }

    /**
     * @return short language tag of the user, f.e. "en"
     */
    String getLanguageTagShort();

    /**
     * @return long language tag of the user, f.e. "en_US"
     */
    String getLanguageTagLong();

    /**
     * @return the underlying player object
     */
    Object getPlayerObject();

    /**
     * @return a list with the user settings
     */
    UserSettings getSettings();

    /**
     * @return a list with the active boss bars for this user
     */
    List<IBossBar> getActiveBossBars();

    /**
     * @return the current user instance as Audience
     */
    Audience asAudience();

    @Override
    default MessagePlaceholders getMessagePlaceholders()
    {
        return this.getStorage().getMessagePlaceholders();
    }
}