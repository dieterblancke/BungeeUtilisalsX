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

package be.dieterblancke.bungeeutilisalsx.spigot.bossbar;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarColor;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarStyle;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.IBossBar;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.spigot.user.SpigotUser;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class BossBar implements IBossBar
{

    private final UUID uuid;
    private final List<User> users;
    private final Set<IEventHandler<UserUnloadEvent>> eventHandlers; // Should only contain ONE EventHandler
    private BarColor color;
    private BarStyle style;
    private float progress;
    private BaseComponent[] message;
    private boolean visible;

    private final org.bukkit.boss.BossBar bossBar;

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
        this.eventHandlers = BuX.getApi().getEventLoader().register( UserUnloadEvent.class, new BossBarListener() );

        this.bossBar = Bukkit.createBossBar(
                this.getMessage(),
                org.bukkit.boss.BarColor.values()[color.getId()],
                org.bukkit.boss.BarStyle.values()[style.getId()]
        );
        this.bossBar.setProgress( progress );

        for ( User user : users )
        {
            this.bossBar.addPlayer( ( (SpigotUser) user ).getPlayer() );
        }
        this.bossBar.setVisible( true );
    }

    @Override
    public void setVisible( boolean visible )
    {
        this.visible = visible;
        this.bossBar.setVisible( visible );
    }

    @Override
    public void setColor( BarColor color )
    {
        this.color = color;
        this.bossBar.setColor( org.bukkit.boss.BarColor.values()[this.color.getId()] );
    }

    @Override
    public void setStyle( BarStyle style )
    {
        this.style = style;
        this.bossBar.setStyle( org.bukkit.boss.BarStyle.values()[this.style.getId()] );
    }

    @Override
    public void setProgress( float progress )
    {
        this.progress = progress;
        this.bossBar.setProgress( progress );
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
            users.add( user );

            bossBar.addPlayer( ( (SpigotUser) user ).getPlayer() );
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
        this.bossBar.setTitle( this.getMessage() );
    }

    @Override
    public void removeUser( User user )
    {
        if ( users.contains( user ) )
        {
            users.remove( user );

            bossBar.removePlayer( ( (SpigotUser) user ).getPlayer() );
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
        for ( User user : this.users )
        {
            this.bossBar.removePlayer( ( (SpigotUser) user ).getPlayer() );
        }
        this.users.clear();
    }

    @Override
    public void unregister()
    {
        eventHandlers.forEach( IEventHandler::unregister );
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