package be.dieterblancke.bungeeutilisalsx.velocity.bossbar;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarColor;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarStyle;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BossBarAction;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.IBossBar;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.BUEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserSettingType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class BossBar implements IBossBar
{

    private final UUID uuid;
    private final List<User> users;
    private final Set<IEventHandler<? extends BUEvent>> eventHandlers; // Should only contain ONE EventHandler
    private BarColor color;
    private BarStyle style;
    private float progress;
    private Component message;
    private boolean visible;

    public BossBar()
    {
        this( UUID.randomUUID(), BarColor.PINK, BarStyle.SOLID, 1F, "" );
    }

    public BossBar( final BarColor color, final BarStyle style, final float progress, final String message )
    {
        this( UUID.randomUUID(), color, style, progress, message );
    }

    public BossBar( final UUID uuid, final BarColor color, final BarStyle style, final float progress, final String message )
    {
        this( uuid, color, style, progress, Utils.format( message ) );
    }

    public BossBar( final UUID uuid, final BarColor color, final BarStyle style, final float progress, final Component message )
    {
        this.uuid = uuid;
        this.color = color;
        this.style = style;
        this.progress = progress;
        this.message = message;
        this.visible = true;
        this.users = Collections.synchronizedList( Lists.newArrayList() );
        this.eventHandlers = BuX.getApi().getEventLoader().register( new BossBarListener(), UserUnloadEvent.class );
    }

    @Override
    public void setVisible( boolean visible )
    {
        this.visible = visible;


        final com.velocitypowered.proxy.protocol.packet.BossBar packet = new com.velocitypowered.proxy.protocol.packet.BossBar();

        if ( visible )
        {
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.ADD.getId() );
            packet.setName( GsonComponentSerializer.gson().serialize( message ) );
            packet.setPercent( progress );
            packet.setColor( color.getId() );
            packet.setOverlay( style.getId() );
        }
        else
        {
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.REMOVE.getId() );
        }

        users.forEach( user -> sendBossBarPacket( user, packet ) );
    }

    @Override
    public void setColor( BarColor color )
    {
        this.color = color;

        if ( visible )
        {
            final com.velocitypowered.proxy.protocol.packet.BossBar packet = new com.velocitypowered.proxy.protocol.packet.BossBar();
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.UPDATE_STYLE.getId() );
            packet.setColor( color.getId() );
            packet.setOverlay( style.getId() );

            users.forEach( user -> sendBossBarPacket( user, packet ) );
        }
    }

    @Override
    public void setStyle( BarStyle style )
    {
        this.style = style;
        if ( visible )
        {
            final com.velocitypowered.proxy.protocol.packet.BossBar packet = new com.velocitypowered.proxy.protocol.packet.BossBar();

            packet.setUuid( uuid );
            packet.setAction( BossBarAction.UPDATE_STYLE.getId() );
            packet.setColor( color.getId() );
            packet.setOverlay( style.getId() );

            users.forEach( user -> sendBossBarPacket( user, packet ) );
        }
    }

    @Override
    public void setProgress( float progress )
    {
        this.progress = progress;
        if ( visible )
        {
            final com.velocitypowered.proxy.protocol.packet.BossBar packet = new com.velocitypowered.proxy.protocol.packet.BossBar();

            packet.setUuid( uuid );
            packet.setAction( BossBarAction.UPDATE_HEALTH.getId() );
            packet.setPercent( progress );

            users.forEach( user -> sendBossBarPacket( user, packet ) );
        }
    }

    @Override
    public Component getBaseComponent()
    {
        return message;
    }

    @Override
    public void addUser( User user )
    {
        if ( !users.contains( user ) )
        {
            if ( user.getVersion() == null || user.getVersion().protocolVersion() < 107 ) // 107 = 1.9 protocol version
            {
                return;
            }
            users.add( user );
            user.getActiveBossBars().add( this );

            final com.velocitypowered.proxy.protocol.packet.BossBar packet = new com.velocitypowered.proxy.protocol.packet.BossBar();
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.ADD.getId() );
            packet.setName( GsonComponentSerializer.gson().serialize( message ) );
            packet.setPercent( progress );
            packet.setColor( color.getId() );
            packet.setOverlay( style.getId() );

            sendBossBarPacket( user, packet );
        }
    }

    @Override
    public String getMessage()
    {
        return LegacyComponentSerializer.legacyAmpersand().serialize( message );
    }

    @Override
    @Deprecated
    public void setMessage( final String message )
    {
        setMessage( Utils.format( message ) );
    }

    @Override
    public void setMessage( Component title )
    {
        this.message = title;
        if ( visible )
        {
            final com.velocitypowered.proxy.protocol.packet.BossBar packet = new com.velocitypowered.proxy.protocol.packet.BossBar();
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.UPDATE_TITLE.getId() );
            packet.setName( GsonComponentSerializer.gson().serialize( message ) );

            users.forEach( user -> sendBossBarPacket( user, packet ) );
        }
    }

    @Override
    public void removeUser( User user )
    {
        if ( users.contains( user ) )
        {
            users.remove( user );
            user.getActiveBossBars().remove( this );

            final com.velocitypowered.proxy.protocol.packet.BossBar packet = new com.velocitypowered.proxy.protocol.packet.BossBar();
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.REMOVE.getId() );
            user.sendPacket( packet );
        }
    }

    @Override
    public boolean hasUser( User user )
    {
        return users.contains( user );
    }

    @Override
    public void clearUsers()
    {
        final com.velocitypowered.proxy.protocol.packet.BossBar packet = new com.velocitypowered.proxy.protocol.packet.BossBar();
        packet.setUuid( uuid );
        packet.setAction( BossBarAction.REMOVE.getId() );

        users.forEach( user ->
        {
            user.sendPacket( packet );
            user.getActiveBossBars().remove( this );
        } );
        users.clear();
    }

    @Override
    public void unregister()
    {
        eventHandlers.forEach( IEventHandler::unregister );
    }

    private void sendBossBarPacket( User user, Object packet )
    {
        boolean bossbarDisabled = user.getSettings().getUserSetting( UserSettingType.BOSSBAR_DISABLED )
                .map( UserSetting::getAsBoolean )
                .orElse( false );

        if ( !bossbarDisabled )
        {
            user.sendPacket( packet );
        }
    }

    private class BossBarListener implements EventExecutor
    {

        @Event
        public void onUnload( UserUnloadEvent event )
        {
            removeUser( event.getUser() );
        }

    }
}