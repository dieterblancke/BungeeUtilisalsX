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

package com.dbsoftwares.bungeeutilisals.bossbar;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BossBarAction;
import com.dbsoftwares.bungeeutilisals.api.bossbar.IBossBar;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.bungeeutilisals.packet.packets.out.PacketPlayOutBossBar;
import com.google.api.client.util.Lists;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class BossBar implements IBossBar
{

    private UUID uuid;
    private BarColor color;
    private BarStyle style;
    private float progress;
    private BaseComponent[] message;
    private boolean visible;

    private List<User> users;
    private Set<EventHandler<UserUnloadEvent>> eventHandlers; // Should only contain ONE EventHandler

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
        this( uuid, color, style, progress, TextComponent.fromLegacyText( message ) );
    }

    public BossBar( final UUID uuid, final BarColor color, final BarStyle style, final float progress, final BaseComponent[] message )
    {
        this.uuid = uuid;
        this.color = color;
        this.style = style;
        this.progress = progress;
        this.message = message;
        this.visible = true;
        this.users = Collections.synchronizedList( Lists.newArrayList() );
        this.eventHandlers = BUCore.getApi().getEventLoader().register( UserUnloadEvent.class, new BossBarListener() );
    }

    @Override
    public void setVisible( boolean visible )
    {
        this.visible = visible;

        final PacketPlayOutBossBar packet;

        if ( visible )
        {
            packet = new PacketPlayOutBossBar();
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.ADD.getId() );
            packet.setTitle( message );
            packet.setPercent( progress );
            packet.setColor( color.getId() );
            packet.setOverlay( style.getId() );
        } else
        {
            packet = new PacketPlayOutBossBar();
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.REMOVE.getId() );
        }

        users.forEach( user -> user.sendPacket( packet ) );
    }

    @Override
    public void setColor( BarColor color )
    {
        this.color = color;

        if ( visible )
        {
            final PacketPlayOutBossBar packet = new PacketPlayOutBossBar();
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.UPDATE_STYLE.getId() );
            packet.setColor( color.getId() );
            packet.setOverlay( style.getId() );

            users.forEach( user -> user.sendPacket( packet ) );
        }
    }

    @Override
    public void setStyle( BarStyle style )
    {
        this.style = style;
        if ( visible )
        {
            final PacketPlayOutBossBar packet = new PacketPlayOutBossBar();
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.UPDATE_STYLE.getId() );
            packet.setColor( color.getId() );
            packet.setOverlay( style.getId() );

            users.forEach( user -> user.sendPacket( packet ) );
        }
    }

    @Override
    public void setProgress( float progress )
    {
        this.progress = progress;
        if ( visible )
        {
            final PacketPlayOutBossBar packet = new PacketPlayOutBossBar();
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.UPDATE_HEALTH.getId() );
            packet.setPercent( progress );

            users.forEach( user -> user.sendPacket( packet ) );
        }
    }

    @Override
    public BaseComponent[] getBaseComponent()
    {
        return message;
    }

    @Override
    public void addUser( User user )
    {
        if ( !users.contains( user ) )
        {
            if ( user.getVersion() == null || user.getVersion().getVersionId() < Version.MINECRAFT_1_9.getVersionId() )
            {
                return;
            }
            users.add( user );

            final PacketPlayOutBossBar packet = new PacketPlayOutBossBar();
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.ADD.getId() );
            packet.setTitle( message );
            packet.setPercent( progress );
            packet.setColor( color.getId() );
            packet.setOverlay( style.getId() );

            user.sendPacket( packet );
        }
    }

    @Override
    public String getMessage()
    {
        return new TextComponent( message ).toLegacyText();
    }

    @Override
    @Deprecated
    public void setMessage( final String message )
    {
        setMessage( TextComponent.fromLegacyText( message ) );
    }

    @Override
    public void setMessage( BaseComponent[] title )
    {
        this.message = title;
        if ( visible )
        {
            final PacketPlayOutBossBar packet = new PacketPlayOutBossBar();
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.UPDATE_TITLE.getId() );
            packet.setTitle( message );

            users.forEach( user -> user.sendPacket( packet ) );
        }
    }

    @Override
    public void removeUser( User user )
    {
        if ( users.contains( user ) )
        {
            users.remove( user );

            final PacketPlayOutBossBar packet = new PacketPlayOutBossBar();
            packet.setUuid( uuid );
            packet.setAction( BossBarAction.REMOVE.getId() );
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
        final PacketPlayOutBossBar packet = new PacketPlayOutBossBar();
        packet.setUuid( uuid );
        packet.setAction( BossBarAction.REMOVE.getId() );

        users.forEach( user -> user.sendPacket( packet ) );
        users.clear();
    }

    @Override
    public void unregister()
    {
        eventHandlers.forEach( EventHandler::unregister );
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