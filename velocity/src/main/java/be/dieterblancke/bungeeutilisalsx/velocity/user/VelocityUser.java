package be.dieterblancke.bungeeutilisalsx.velocity.user;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.IBossBar;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserLoadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSettings;
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.CooldownConstants;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserCooldowns;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserSettings;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MessageUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.VersionsConfig.Version;
import be.dieterblancke.bungeeutilisalsx.velocity.Bootstrap;
import be.dieterblancke.bungeeutilisalsx.velocity.utils.VelocityPacketUtils;
import be.dieterblancke.bungeeutilisalsx.velocity.utils.VelocityServer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.net.InetSocketAddress;
import java.util.*;

@Setter
@Getter
public class VelocityUser implements User
{

    private final List<IBossBar> activeBossBars = Collections.synchronizedList( new ArrayList<>() );
    private Player player;
    private String name;
    private UUID uuid;
    private String ip;
    private UserCooldowns cooldowns;
    private UserStorage storage;
    private boolean socialSpy;
    private boolean commandSpy;
    private List<FriendData> friends = Lists.newArrayList();
    private FriendSettings friendSettings = new FriendSettings();
    private boolean inStaffChat;
    private boolean msgToggled;
    private String group;
    private UserSettings userSettings;

    @Override
    public void load( final Object playerInstance )
    {
        final Date now = new Date();
        final Dao dao = BuX.getInstance().getAbstractStorageManager().getDao();

        this.player = (Player) playerInstance;
        this.name = player.getUsername();
        this.uuid = player.getUniqueId();
        this.ip = Utils.getIP( player.getRemoteAddress() );
        this.cooldowns = new UserCooldowns();
        this.storage = new UserStorage(
                uuid,
                name,
                ip,
                BuX.getApi().getLanguageManager().getDefaultLanguage(),
                now,
                now,
                Lists.newArrayList(),
                this.getJoinedHost(),
                Maps.newHashMap()
        );
        this.userSettings = new UserSettings( uuid, new ArrayList<>() );

        dao.getUserDao().getUserData( uuid ).thenAccept( ( userStorage ) ->
        {
            if ( userStorage.isPresent() )
            {
                storage = userStorage.get();

                if ( !storage.getUserName().equalsIgnoreCase( name ) )
                {
                    dao.getUserDao().setName( uuid, name );
                    storage.setUserName( name );
                }

                if ( BuX.getApi().getLanguageManager().useCustomIntegration() )
                {
                    storage.setLanguage( BuX.getApi().getLanguageManager().getLanguageIntegration().getLanguage( uuid ) );
                }

                if ( storage.getJoinedHost() == null )
                {
                    final String joinedHost = this.getJoinedHost();

                    storage.setJoinedHost( joinedHost );
                    dao.getUserDao().setJoinedHost( uuid, joinedHost );
                }
            }
            else
            {
                final String joinedHost = this.getJoinedHost();
                final Language language = BuX.getApi().getLanguageManager().getDefaultLanguage();

                dao.getUserDao().createUser(
                        uuid,
                        name,
                        ip,
                        language,
                        joinedHost
                );
            }
        } );
        dao.getUserDao().getSettings( uuid ).thenAccept( settings -> userSettings = settings );

        if ( ConfigFiles.FRIENDS_CONFIG.isEnabled() )
        {
            dao.getFriendsDao().getFriends( uuid ).thenAccept( friendsList -> friends = friendsList );
            dao.getFriendsDao().getSettings( uuid ).thenAccept( settings -> friendSettings = settings );

            BuX.debug( "Friend list of " + name );
            BuX.debug( Arrays.toString( friends.toArray() ) );
        }
        else
        {
            friendSettings = new FriendSettings();
        }

        BuX.getInstance().getActivePermissionIntegration().getGroup( uuid ).thenAccept( group -> this.group = group );
        BuX.getInstance().getScheduler().runTaskDelayed( 15, TimeUnit.SECONDS, this::sendOfflineMessages );
        BuX.getApi().getEventLoader().launchEventAsync( new UserLoadEvent( this ) );
    }

    @Override
    public void unload()
    {
        BuX.getApi().getEventLoader().launchEvent( new UserUnloadEvent( this ) );
        this.save( true );

        // clearing data from memory
        cooldowns.remove();
        player = null;
        storage.getData().clear();
    }

    @Override
    public void save( final boolean logout )
    {
        BuX.getInstance().getAbstractStorageManager().getDao().getUserDao().updateUser(
                uuid,
                getName(),
                ip,
                getLanguage(),
                logout ? new Date() : null
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
        sendMessage( MessageUtils.fromTextNoColors( PlaceHolderAPI.formatMessage( this, message ) ) );
    }

    @Override
    public void sendRawColorMessage( String message )
    {
        sendMessage( Utils.format( this, message ) );
    }

    @Override
    public void sendMessage( Component component )
    {
        if ( this.isEmpty( component ) )
        {
            return;
        }

        player.sendMessage( component );
    }

    @Override
    public void kick( String reason )
    {
        BuX.getInstance().getScheduler().runAsync( () -> forceKick( reason ) );
    }

    @Override
    public void forceKick( String reason )
    {
        this.player.disconnect( Utils.format( this, reason ) );
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
        if ( this.player == null )
        {
            return "";
        }
        return this.player.getCurrentServer().map( it -> it.getServerInfo().getName() ).orElse( "" );
    }

    @Override
    public void sendToServer( IProxyServer proxyServer )
    {
        this.cooldowns.updateTime( CooldownConstants.SERVER_SWITCH_SERVER_BALANCER_COOLDOWN, TimeUnit.SECONDS, 5 );
        this.player.createConnectionRequest( ( (VelocityServer) proxyServer ).getRegisteredServer() ).fireAndForget();
    }

    @Override
    public Version getVersion()
    {
        try
        {
            return ConfigFiles.VERSIONS_CONFIG.getVersion( player.getProtocolVersion().getProtocol() );
        }
        catch ( Exception e )
        {
            return ConfigFiles.VERSIONS_CONFIG.getUnknownVersion();
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
    public void executeCommand( final String command )
    {
        Bootstrap.getInstance().getProxyServer().getCommandManager().executeImmediatelyAsync( player, command );
    }

    @Override
    public void sendPacket( final Object packet )
    {
        VelocityPacketUtils.sendPacket( player, packet );
    }

    @Override
    public String getJoinedHost()
    {
        final String joinedHost;
        if ( player.getVirtualHost().isEmpty() )
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
    public void setVanished( boolean vanished )
    {
        // do nothing
    }

    @Override
    public String getLanguageTagShort()
    {
        return Optional.ofNullable( player.getPlayerSettings().getLocale() )
                .map( Locale::toString )
                .orElse( "en" );
    }

    @Override
    public String getLanguageTagLong()
    {
        return Optional.ofNullable( player.getPlayerSettings().getLocale() )
                .map( Locale::toString )
                .orElse( "en_US" );
    }

    @Override
    public Object getPlayerObject()
    {
        return player;
    }

    @Override
    public UserSettings getSettings()
    {
        return userSettings;
    }

    @Override
    public Audience asAudience()
    {
        return this.player;
    }

    @Override
    public boolean allowsMessageModifications()
    {
        return player.getIdentifiedKey() == null
                || player.getProtocolVersion().getProtocol() < 760;
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