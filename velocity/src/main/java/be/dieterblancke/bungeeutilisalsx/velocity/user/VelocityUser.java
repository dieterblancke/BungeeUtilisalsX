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

package be.dieterblancke.bungeeutilisalsx.velocity.user;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserLoadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSettings;
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.language.LanguageConfig;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.MessageQueue;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserCooldowns;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Version;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.QueuedMessage;
import be.dieterblancke.bungeeutilisalsx.velocity.Bootstrap;
import be.dieterblancke.bungeeutilisalsx.velocity.utils.VelocityPacketUtils;
import be.dieterblancke.bungeeutilisalsx.velocity.utils.VelocityServer;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.net.InetSocketAddress;
import java.sql.Date;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Setter
@Getter
public class VelocityUser implements User
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
        final Dao dao = BuX.getInstance().getAbstractStorageManager().getDao();

        this.player = Bootstrap.getInstance().getProxyServer().getPlayer( uuid ).orElseThrow(
                () -> new RuntimeException( "Could not find player with uuid " + uuid + " on join." )
        );
        this.name = player.getUsername();
        this.uuid = player.getUniqueId();
        this.ip = Utils.getIP( player.getRemoteAddress() );
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
        {
            // Stored name != user current name | Name changed?
            storage.setUserName( name );
            dao.getUserDao().setName( uuid, name );
        }

        if ( ConfigFiles.FRIENDS_CONFIG.isEnabled() )
        {
            friends = dao.getFriendsDao().getFriends( uuid );
            friendSettings = dao.getFriendsDao().getSettings( uuid );

            BuX.debug( "Friend list of " + name );
            BuX.debug( Arrays.toString( friends.toArray() ) );
        }
        else
        {
            friendSettings = new FriendSettings();
        }

        BuX.getInstance().getScheduler().runAsync( () ->
        {
            messageQueue = BuX.getApi().getStorageManager().getDao().createMessageQueue(
                    player.getUniqueId(), player.getUsername(), ip
            );
            executeMessageQueue();
        } );

        final UserLoadEvent userLoadEvent = new UserLoadEvent( this );
        BuX.getApi().getEventLoader().launchEvent( userLoadEvent );
    }

    @Override
    public void unload()
    {
        final UserUnloadEvent event = new UserUnloadEvent( this );
        BuX.getApi().getEventLoader().launchEvent( event );

        save();
        cooldowns.remove();

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
    public void sendMessage( BaseComponent component )
    {
        if ( this.isEmpty( component ) )
        {
            return;
        }
        VelocityPacketUtils.sendMessagePacket( player, component );
    }

    @Override
    public void sendMessage( BaseComponent[] components )
    {
        if ( this.isEmpty( components ) )
        {
            return;
        }
        VelocityPacketUtils.sendMessagePacket( player, components );
    }

    @Override
    public void kick( String reason )
    {
        BuX.getInstance().getScheduler().runAsync( () -> forceKick( reason ) );
    }

    @Override
    public void langKick( String path, Object... placeholders )
    {
        if ( getLanguageConfig().getConfig().isList( path ) )
        {
            final String reason = getLanguageConfig().getConfig().getStringList( path ).stream().map( str ->
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
            String message = getLanguageConfig().getConfig().getString( path );
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
        getPlayer().disconnect( Component.text( Utils.c( reason ) ) );
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

    public Player getPlayer()
    {
        return player;
    }

    @Override
    public int getPing()
    {
        return (int) player.getPing();
    }

    @Override
    public boolean isConsole()
    {
        return false;
    }

    @Override
    public String getServerName()
    {
        if ( getPlayer() == null || !getPlayer().getCurrentServer().isPresent() )
        {
            return "";
        }
        return getPlayer().getCurrentServer().get().getServerInfo().getName();
    }

    @Override
    public void sendToServer( IProxyServer proxyServer )
    {
        this.player.createConnectionRequest( ( (VelocityServer) proxyServer ).getRegisteredServer() ).fireAndForget();
    }

    @Override
    public Version getVersion()
    {
        try
        {
            return Version.getVersion( player.getProtocolVersion().getProtocol() );
        }
        catch ( Exception e )
        {
            return Version.MINECRAFT_1_8;
        }
    }

    @Override
    public boolean hasPermission( final String permission )
    {
        return hasPermission( permission, false );
    }

    @Override
    public boolean hasPermission( String permission, boolean specific )
    {
        return specific
                ? this.hasAnyPermission( permission )
                : this.hasAnyPermission( permission, "*", "bungeeutilisalsx.*" );
    }

    @Override
    public boolean hasAnyPermission( final String... permissions )
    {
        try
        {
            for ( String permission : permissions )
            {
                if ( player.hasPermission( permission ) )
                {
                    if ( ConfigFiles.CONFIG.isDebug() )
                    {
                        BuX.getLogger().info( String.format( "%s has the permission %s", this.getName(), permission ) );
                    }
                    return true;
                }
                else
                {
                    if ( ConfigFiles.CONFIG.isDebug() )
                    {
                        BuX.getLogger().info( String.format( "%s does not have the permission %s", this.getName(), permission ) );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            BuX.getLogger().info( "Failed to check permission " + Arrays.toString( permissions ) + " for " + name + " due to an error that occured!" );
            return false;
        }
        return false;
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
        Bootstrap.getInstance().getProxyServer().getCommandManager().executeImmediatelyAsync( player, command );
    }

    @Override
    public void sendActionBar( final String actionbar )
    {
        player.sendActionBar( Component.text( Utils.formatString( this, actionbar ) ) );
    }

    @Override
    public void sendTitle( final String title, final String subtitle, final int fadein, final int stay, final int fadeout )
    {
        player.showTitle( Title.title(
                Component.text( Utils.formatString( this, title ) ),
                Component.text( Utils.formatString( this, subtitle ) ),
                Title.Times.of( Duration.ofSeconds( fadein ), Duration.ofSeconds( stay ), Duration.ofSeconds( fadeout ) )
        ) );
    }

    @Override
    public void sendPacket( final Object packet )
    {
        VelocityPacketUtils.sendPacket( player, packet );
    }

    @Override
    public void setTabHeader( final BaseComponent[] header, final BaseComponent[] footer )
    {
        VelocityPacketUtils.sendTabPacket( player, header, footer );
    }

    @Override
    public String getJoinedHost()
    {
        final String joinedHost;
        if ( !player.getVirtualHost().isPresent() )
        {
            joinedHost = null;
        }
        else
        {
            final InetSocketAddress virtualHost = player.getVirtualHost().get();

            if ( virtualHost.getHostName() == null )
            {
                joinedHost = Utils.getIP( virtualHost.getAddress() );
            }
            else
            {
                joinedHost = virtualHost.getHostName();
            }
        }
        return joinedHost;
    }

    @Override
    public boolean isVanished()
    {
        return false;
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

        VelocityUser user = (VelocityUser) o;
        return user.name.equalsIgnoreCase( name ) && user.uuid.equals( uuid );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, uuid );
    }
}