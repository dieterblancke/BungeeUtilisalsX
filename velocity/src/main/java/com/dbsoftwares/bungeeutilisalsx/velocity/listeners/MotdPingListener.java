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

package com.dbsoftwares.bungeeutilisalsx.velocity.listeners;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Version;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.motd.ConditionHandler;
import com.dbsoftwares.bungeeutilisalsx.common.motd.MotdData;
import com.dbsoftwares.bungeeutilisalsx.velocity.utils.VelocityMotdConnection;
import com.google.common.collect.Lists;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MotdPingListener
{

    private static final UUID EMPTY_UUID = new UUID( 0, 0 );

    @Subscribe
    public void onPing( final ProxyPingEvent event )
    {
        final List<MotdData> dataList = ConfigFiles.MOTD.getMotds();
        final VelocityMotdConnection motdConnection = this.getConnection( event.getConnection() );

        ServerPing result = loadConditionalMotd( event, dataList, motdConnection );
        if ( result == null )
        {
            result = loadDefaultMotd( event, dataList, motdConnection );
        }

        if ( result != null )
        {
            event.setPing( result );
        }
    }

    private ServerPing loadMotd( final ProxyPingEvent event, final MotdData motd, final VelocityMotdConnection motdConnection )
    {
        if ( motd == null )
        {
            return null;
        }
        final ServerPing orig = event.getPing();
        final String message = formatMessage( motd.getMotd(), event, motdConnection );
        final Component component = GsonComponentSerializer.gson().deserialize(
                ComponentSerializer.toString( Utils.format( message ) )
        );

        final List<ServerPing.SamplePlayer> hoverMessages = Lists.newArrayList();
        for ( String hoverMessage : motd.getHoverMessages() )
        {
            hoverMessages.add( new ServerPing.SamplePlayer(
                    Utils.c( formatMessage( hoverMessage, event, motdConnection ) ),
                    EMPTY_UUID
            ) );
        }

        return new ServerPing(
                new ServerPing.Version( orig.getVersion().getProtocol(), orig.getVersion().getName() ),
                new ServerPing.Players(
                        orig.getPlayers().map( ServerPing.Players::getOnline ).orElse( 0 ),
                        orig.getPlayers().map( ServerPing.Players::getMax ).orElse( 0 ),
                        hoverMessages
                ),
                component,
                orig.getFavicon().orElse( null )
        );
    }

    private String formatMessage( String message, final ProxyPingEvent event, final VelocityMotdConnection motdConnection )
    {
        final Version version = Version.getVersion( event.getConnection().getProtocolVersion().getProtocol() );

        message = message.replace( "{user}", motdConnection.getName() == null ? "Unknown" : motdConnection.getName() );
        message = message.replace( "{version}", version == null ? "Unknown" : version.toString() );

        if ( !event.getConnection().getVirtualHost().isPresent() || event.getConnection().getVirtualHost().get().getHostName() == null )
        {
            message = message.replace( "{domain}", "Unknown" );
        }
        else
        {
            message = message.replace( "{domain}", event.getConnection().getVirtualHost().get().getHostName() );
        }
        message = PlaceHolderAPI.formatMessage( message );

        return message;
    }

    private ServerPing loadDefaultMotd( final ProxyPingEvent event, final List<MotdData> motds, final VelocityMotdConnection motdConnection )
    {
        final List<MotdData> defMotds = motds.stream().filter( MotdData::isDef ).collect( Collectors.toList() );
        final MotdData motd = MathUtils.getRandomFromList( defMotds );

        return loadMotd( event, motd, motdConnection );
    }

    private ServerPing loadConditionalMotd( final ProxyPingEvent event, final List<MotdData> motds, final VelocityMotdConnection motdConnection )
    {
        final List<MotdData> conditions = motds.stream().filter( data -> !data.isDef() ).collect( Collectors.toList() );

        for ( final MotdData condition : conditions )
        {
            final ConditionHandler handler = condition.getConditionHandler();

            if ( handler.checkCondition( motdConnection ) )
            {
                final List<MotdData> conditionalMotds = conditions.stream().filter(
                        data -> data.getConditionHandler().getCondition().equalsIgnoreCase( handler.getCondition() )
                ).collect( Collectors.toList() );
                final MotdData motd = MathUtils.getRandomFromList( conditionalMotds );

                return loadMotd( event, motd, motdConnection );
            }
        }
        return null;
    }

    // Name not known on serverlist ping, so we're loading the last seen name on the IP as the initial connection name.
    private VelocityMotdConnection getConnection( final InboundConnection connection )
    {
        final String name = BuX.getApi().getStorageManager().getDao().getUserDao()
                .getUsersOnIP( Utils.getIP( connection.getRemoteAddress() ) )
                .stream()
                .findFirst()
                .orElse( null );

        return new VelocityMotdConnection( connection, name );
    }
}