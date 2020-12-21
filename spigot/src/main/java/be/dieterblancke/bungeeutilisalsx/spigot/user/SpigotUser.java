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

package be.dieterblancke.bungeeutilisalsx.spigot.user;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserLoadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSettings;
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.MessageQueue;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserCooldowns;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.CanReceiveMessages;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MessageBuilder;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Version;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.QueuedMessage;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
@Getter
public class SpigotUser implements User, CanReceiveMessages
{

    private Player player;

    private String name;
    private UUID uuid;
    private String ip;
    private UserCooldowns cooldowns;
    private UserStorage storage;
    private List<PunishmentInfo> mute;
    private boolean socialSpy;
    private boolean commandSpy;
    private List<FriendData> friends = Lists.newArrayList();
    private FriendSettings friendSettings;
    private boolean inStaffChat;
    private MessageQueue<QueuedMessage> messageQueue;
    private boolean msgToggled;

    @Override
    public void load( UUID uuid )
    {
        this.player = Bukkit.getPlayer( uuid );
        this.load( player, player.getAddress().getAddress() );
    }

    public void load( final Player player, final InetAddress address )
    {
        final Dao dao = BuX.getInstance().getAbstractStorageManager().getDao();

        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.ip = Utils.getIP( address );
        this.storage = new UserStorage();
        this.cooldowns = new UserCooldowns();

        if ( dao.getUserDao().exists( uuid ) )
        {
            storage = dao.getUserDao().getUserData( uuid );

            if ( !storage.getUserName().equalsIgnoreCase( name ) )
            {
                dao.getUserDao().setName( uuid, name );
                storage.setUserName( name );
            }

            storage.setLanguage( BuX.getApi().getLanguageManager().getLanguageIntegration().getLanguage( uuid ) );

            if ( storage.getJoinedHost() == null )
            {
                final String joinedHost = this.getJoinedHost();

                storage.setJoinedHost( joinedHost );
                dao.getUserDao().setJoinedHost( uuid, joinedHost );
            }
        }
        else
        {
            final Language defLanguage = BuX.getApi().getLanguageManager().getDefaultLanguage();
            final Date date = new Date( System.currentTimeMillis() );
            final String joinedHost = this.getJoinedHost();

            dao.getUserDao().createUser(
                    uuid,
                    name,
                    ip,
                    defLanguage,
                    joinedHost
            );

            storage = new UserStorage( uuid, name, ip, defLanguage, date, date, Lists.newArrayList(), joinedHost, Maps.newHashMap() );
        }

        if ( !storage.getUserName().equals( name ) )
        { // Stored name != user current name | Name changed?
            storage.setUserName( name );
            dao.getUserDao().setName( uuid, name );
        }

        if ( ConfigFiles.FRIENDS_CONFIG.isEnabled() )
        {
            friends = dao.getFriendsDao().getFriends( uuid );
            friendSettings = dao.getFriendsDao().getSettings( uuid );

            if ( ConfigFiles.CONFIG.isDebug() )
            {
                System.out.println( "Friend list of " + name );
                System.out.println( Arrays.toString( friends.toArray() ) );
            }
        }
        else
        {
            friendSettings = new FriendSettings();
        }

        BuX.getInstance().getScheduler().runAsync( () ->
        {
            messageQueue = BuX.getApi().getStorageManager().getDao().createMessageQueue(
                    player.getUniqueId(), player.getName(), ip
            );
            executeMessageQueue();
        } );

        final UserLoadEvent userLoadEvent = new UserLoadEvent( this );
        BuX.getApi().getEventLoader().launchEvent( userLoadEvent );
    }

    @Override
    public void unload()
    {
        save();
        cooldowns.remove();

        final UserUnloadEvent event = new UserUnloadEvent( this );
        BuX.getApi().getEventLoader().launchEvent( event );

        player = null;
        storage.getData().clear();
    }

    @Override
    public void save()
    {
        BuX.getInstance().getAbstractStorageManager().getDao().getUserDao().updateUser(
                uuid, getName(), ip, getLanguage(), new Date( System.currentTimeMillis() )
        );
    }

    @Override
    public UserStorage getStorage()
    {
        return storage;
    }

    @Override
    public UserCooldowns getCooldowns()
    {
        return cooldowns;
    }

    @Override
    public String getIp()
    {
        return ip;
    }

    @Override
    public Language getLanguage()
    {
        return storage.getLanguage();
    }

    @Override
    public void setLanguage( Language language )
    {
        storage.setLanguage( language );
    }

    @Override
    public void sendRawMessage( String message )
    {
        if ( message.isEmpty() )
        {
            return;
        }
        sendMessage( TextComponent.fromLegacyText( PlaceHolderAPI.formatMessage( this, message ) ) );
    }

    @Override
    public void sendRawColorMessage( String message )
    {
        sendMessage( Utils.format( this, message ) );
    }

    @Override
    public void sendMessage( String message )
    {
        if ( message.isEmpty() )
        {
            return;
        }
        sendMessage( getLanguageConfig().getString( "prefix" ), PlaceHolderAPI.formatMessage( this, message ) );
    }

    @Override
    public void sendLangMessage( String path )
    {
        sendLangMessage( true, path );
    }

    @Override
    public void sendLangMessage( String path, Object... placeholders )
    {
        sendLangMessage( true, path, placeholders );
    }

    @Override
    public void sendLangMessage( boolean prefix, String path )
    {
        sendLangMessage( prefix, path, new Object[0] );
    }

    @Override
    public void sendLangMessage( boolean prefix, String path, Object... placeholders )
    {
        this.sendLangMessage( path, prefix, null, null, placeholders );
    }

    @Override
    public void sendLangMessage( final String path,
                                 boolean prefix,
                                 final Function<String, String> prePlaceholderFormatter,
                                 final Function<String, String> postPlaceholderFormatter,
                                 final Object... placeholders )
    {
        if ( getLanguageConfig().isSection( path ) )
        {
            // section detected, assuming this is a message to be handled by MessageBuilder (hover / focus events)
            final TextComponent component = MessageBuilder.buildMessage(
                    this, getLanguageConfig().getSection( path ), prePlaceholderFormatter, postPlaceholderFormatter, placeholders
            );

            sendMessage( component );
            return;
        }

        String message = buildLangMessage( path, prePlaceholderFormatter, postPlaceholderFormatter, placeholders );

        if ( message.isEmpty() )
        {
            return;
        }

        if ( message.startsWith( "noprefix: " ) )
        {
            prefix = false;
            message = message.replaceFirst( "noprefix: ", "" );
        }

        if ( prefix )
        {
            sendMessage( message );
        }
        else
        {
            sendRawColorMessage( message );
        }
    }

    @Override
    public void sendMessage( String prefix, String message )
    {
        sendMessage( Utils.format( prefix + PlaceHolderAPI.formatMessage( this, message ) ) );
    }

    @Override
    public void sendMessage( BaseComponent component )
    {
        if ( this.isEmpty( component ) )
        {
            return;
        }
        getPlayer().spigot().sendMessage( component );
    }

    @Override
    public void sendMessage( BaseComponent[] components )
    {
        if ( this.isEmpty( components ) )
        {
            return;
        }
        getPlayer().spigot().sendMessage( components );
    }

    @Override
    public void kick( String reason )
    {
        BuX.getInstance().getScheduler().runAsync( () -> getPlayer().kickPlayer( Utils.c( reason ) ) );
    }

    @Override
    public void langKick( String path, Object... placeholders )
    {
        if ( getLanguageConfig().isList( path ) )
        {
            final String reason = getLanguageConfig().getStringList( path ).stream().map( str ->
            {
                for ( int i = 0; i < placeholders.length - 1; i += 2 )
                {
                    str = str.replace( placeholders[i].toString(), placeholders[i + 1].toString() );
                }
                return str;
            } ).collect( Collectors.joining( "\n" ) );

            kick( reason );
        }
        else
        {
            String message = getLanguageConfig().getString( path );
            for ( int i = 0; i < placeholders.length - 1; i += 2 )
            {
                message = message.replace( placeholders[i].toString(), placeholders[i + 1].toString() );
            }

            kick( message );
        }
    }

    @Override
    public void forceKick( String reason )
    {
        getPlayer().kickPlayer( Utils.c( reason ) );
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public UUID getUuid()
    {
        return uuid;
    }

    @Override
    public void sendNoPermMessage()
    {
        sendLangMessage( "no-permission" );
    }

    @Override
    public int getPing()
    {
        return 0;
    }

    @Override
    public IConfiguration getLanguageConfig()
    {
        return BuX.getApi().getLanguageManager().getLanguageConfiguration( BuX.getInstance().getName(), this );
    }

    @Override
    public boolean isConsole()
    {
        return false;
    }

    @Override
    public String getServerName()
    {
        return "";
    }

    @Override
    public void sendToServer( IProxyServer proxyServer )
    {
        // do nothing
    }

    @Override
    public Version getVersion()
    {
        return Version.LEGACY;
    }

    @Override
    public String buildLangMessage( final String path, final Object... placeholders )
    {
        return this.buildLangMessage( path, null, null, placeholders );
    }

    @Override
    public String buildLangMessage(
            final String path,
            final Function<String, String> prePlaceholderFormatter,
            final Function<String, String> postPlaceholderFormatter,
            final Object... placeholders )
    {
        if ( !getLanguageConfig().exists( path ) )
        {
            return "";
        }
        final StringBuilder builder = new StringBuilder();

        if ( getLanguageConfig().isList( path ) )
        {
            final List<String> messages = getLanguageConfig().getStringList( path );

            if ( messages.isEmpty() )
            {
                return "";
            }

            for ( int i = 0; i < messages.size(); i++ )
            {
                final String message = replacePlaceHolders(
                        messages.get( i ),
                        prePlaceholderFormatter,
                        postPlaceholderFormatter,
                        placeholders
                );
                builder.append( message );

                if ( i < messages.size() - 1 )
                {
                    builder.append( "\n" );
                }
            }
        }
        else
        {
            final String message = replacePlaceHolders(
                    getLanguageConfig().getString( path ),
                    prePlaceholderFormatter,
                    postPlaceholderFormatter,
                    placeholders
            );

            if ( message.isEmpty() )
            {
                return "";
            }

            builder.append( message );
        }
        return builder.toString();
    }

    @Override
    public boolean hasPermission( final String permission )
    {
        return hasPermission( permission, false );
    }

    @Override
    public boolean hasPermission( String permission, boolean specific )
    {
        final boolean hasPermission;

        if ( specific )
        {
            hasPermission = player.hasPermission( permission );
        }
        else
        {
            hasPermission = player.hasPermission( permission )
                    || player.hasPermission( "*" )
                    || player.hasPermission( "bungeeutilisalsx.*" );
        }

        if ( ConfigFiles.CONFIG.isDebug() )
        {
            if ( hasPermission )
            {
                BuX.getLogger().info( String.format( "%s has the permission %s", this.getName(), permission ) );
            }
            else
            {
                BuX.getLogger().info( String.format( "%s does not have the permission %s", this.getName(), permission ) );
            }
        }

        return hasPermission;
    }

    @Override
    public void executeMessageQueue()
    {
        QueuedMessage message = messageQueue.poll();

        while ( message != null )
        {
            sendLangMessage( message.getMessage().getLanguagePath(), message.getMessage().getPlaceHolders() );

            message = messageQueue.poll();
        }
    }

    @Override
    public void executeCommand( final String command )
    {
        Bukkit.dispatchCommand( player, command );
    }

    @Override
    public void sendActionBar( final String actionbar )
    {
        player.spigot().sendMessage( ChatMessageType.ACTION_BAR, Utils.format( actionbar ) );
    }

    @Override
    public void sendTitle( final String title, final String subtitle, final int fadein, final int stay, final int fadeout )
    {
        // do nothing
    }

    @Override
    public void sendPacket( final Object packet )
    {
        // do nothing
    }

    @Override
    public void setTabHeader( final BaseComponent[] header, final BaseComponent[] footer )
    {
        // do nothing
    }

    @Override
    public String getJoinedHost()
    {
        return "";
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final SpigotUser user = (SpigotUser) o;
        return user.name.equalsIgnoreCase( name ) && user.uuid.equals( uuid );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, uuid );
    }
}