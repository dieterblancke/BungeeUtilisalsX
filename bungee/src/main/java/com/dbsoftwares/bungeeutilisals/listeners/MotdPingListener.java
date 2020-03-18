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

package com.dbsoftwares.bungeeutilisals.listeners;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.motd.ConditionHandler;
import com.dbsoftwares.bungeeutilisals.api.motd.MotdData;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.ReflectionUtils;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MotdPingListener implements Listener
{

    private static final UUID EMPTY_UUID = new UUID( 0, 0 );

    @EventHandler
    public void onPing( ProxyPingEvent event )
    {
        final List<MotdData> dataList = FileLocation.MOTD.getDataList();

        insertName( event.getConnection() );

        if ( !loadConditionalMotd( event, dataList ) )
        {
            loadDefaultMotd( event, dataList );
        }
    }

    private void loadMotd( final ProxyPingEvent event, final MotdData motd )
    {
        if ( motd == null )
        {
            return;
        }
        final String message = formatMessage( motd.getMotd(), event );
        final BaseComponent component = new TextComponent( Utils.format( message ) );

        event.getResponse().setDescriptionComponent( component );

        final List<ServerPing.PlayerInfo> hoverMessages = Lists.newArrayList();
        for ( String hoverMessage : motd.getHoverMessages() )
        {
            hoverMessages.add( new ServerPing.PlayerInfo(
                    Utils.c( formatMessage( hoverMessage, event ) ),
                    EMPTY_UUID
            ) );
        }
        event.getResponse().getPlayers().setSample( hoverMessages.toArray( new ServerPing.PlayerInfo[0] ) );
    }

    private String formatMessage( String message, final ProxyPingEvent event )
    {
        final Version version = Version.getVersion( event.getConnection().getVersion() );

        message = message.replace( "{user}", event.getConnection().getName() == null ? "Unknown" : event.getConnection().getName() );
        message = message.replace( "{version}", version == null ? "Unknown" : version.toString() );

        if ( event.getConnection().getVirtualHost() == null || event.getConnection().getVirtualHost().getHostName() == null )
        {
            message = message.replace( "{domain}", "Unknown" );
        }
        else
        {
            message = message.replace( "{domain}", event.getConnection().getVirtualHost().getHostName() );
        }
        message = PlaceHolderAPI.formatMessage( message );

        return message;
    }

    private void loadDefaultMotd( final ProxyPingEvent event, final List<MotdData> motds )
    {
        final List<MotdData> defMotds = motds.stream().filter( MotdData::isDef ).collect( Collectors.toList() );
        final MotdData motd = MathUtils.getRandomFromList( defMotds );

        loadMotd( event, motd );
    }

    private boolean loadConditionalMotd( final ProxyPingEvent event, final List<MotdData> motds )
    {
        final List<MotdData> conditions = motds.stream().filter( data -> !data.isDef() ).collect( Collectors.toList() );

        for ( final MotdData condition : conditions )
        {
            final ConditionHandler handler = condition.getConditionHandler();

            if ( handler.checkCondition( event.getConnection() ) )
            {
                final List<MotdData> conditionalMotds = conditions.stream().filter(
                        data -> data.getConditionHandler().getCondition().equalsIgnoreCase( handler.getCondition() )
                ).collect( Collectors.toList() );
                final MotdData motd = MathUtils.getRandomFromList( conditionalMotds );

                loadMotd( event, motd );
                return true;
            }
        }
        return false;
    }

    // Name not known on serverlist ping, so we're loading the last seen name on the IP as the initial connection name.
    private void insertName( PendingConnection connection )
    {
        try
        {
            final String name = BUCore.getApi().getStorageManager().getDao().getUserDao()
                    .getUsersOnIP( Utils.getIP( connection.getAddress() ) ).stream().findFirst().orElse( null );

            if ( name == null )
            {
                return;
            }
            final Field nameField = ReflectionUtils.getField( connection.getClass(), "name" );

            if ( nameField != null )
            {
                nameField.set( connection, name );
            }
        }
        catch ( Exception e )
        {
            // do nothing
        }
    }
}
