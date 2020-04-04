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

package com.dbsoftwares.bungeeutilisals.api.bridge.impl;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.bridge.Bridge;
import com.dbsoftwares.bungeeutilisals.api.bridge.BridgeType;
import com.dbsoftwares.bungeeutilisals.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisals.api.bridge.message.BridgedMessage;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PluginMessageBridge extends Bridge implements Listener
{

    @Override
    public boolean setup()
    {
        try
        {
            ProxyServer.getInstance().registerChannel( "bungeeutilisalsx:default-channel" );
            ProxyServer.getInstance().getPluginManager().registerListener( BUCore.getApi().getPlugin(), this );
            eventHandlers = BUCore.getApi().getEventLoader().register( BridgeResponseEvent.class, this );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return setup = false;
        }
        return setup = true;
    }

    @Override
    public BridgedMessage sendMessage(
            final BridgeType type,
            final String action,
            final Object data
    )
    {
        // untargeted message, this means we should send it to all servers
        final List<String> targets = ProxyServer.getInstance().getServers()
                .values()
                .stream()
                .map( ServerInfo::getName )
                .collect( Collectors.toList() );

        return sendTargetedMessage( type, targets, null, action, data );
    }

    @Override
    public <T> BridgedMessage sendMessage(
            final BridgeType type,
            final String action,
            final Object data,
            final Class<T> responseType,
            final Consumer<T> consumer
    )
    {
        // untargeted message, this means we should send it to all servers
        final List<String> targets = ProxyServer.getInstance().getServers()
                .values()
                .stream()
                .map( ServerInfo::getName )
                .collect( Collectors.toList() );

        return sendTargetedMessage( type, targets, null, action, data, responseType, consumer );
    }

    @Override
    public BridgedMessage sendTargetedMessage(
            final BridgeType type,
            final List<String> targets,
            final List<String> ignoredTargets,
            final String action,
            final Object data
    )
    {
        final BridgedMessage message = new BridgedMessage(
                type,
                UUID.randomUUID(),
                ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ),
                targets,
                ignoredTargets,
                action,
                data
        );

        this.sendMessage( message );
        return message;
    }

    @Override
    public <T> BridgedMessage sendTargetedMessage(
            final BridgeType type,
            final List<String> targets,
            final List<String> ignoredTargets,
            final String action,
            final Object data,
            final Class<T> responseType,
            final Consumer<T> consumer
    )
    {
        final BridgedMessage message = new BridgedMessage(
                type,
                UUID.randomUUID(),
                ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ),
                targets,
                ignoredTargets,
                action,
                data
        );

        consumersMap.put( message.getIdentifier().toString(), new AbstractMap.SimpleEntry<>( responseType, consumer ) );

        this.sendMessage( message );
        return message;
    }

    private void sendMessage( final BridgedMessage message )
    {
        final List<String> targetServers = message.getTargets();

        if ( targetServers == null || targetServers.isEmpty() )
        {
            consumersMap.remove( message.getIdentifier().toString() );
            return;
        }
        final String jsonMessage = BUCore.getGson().toJson( message );

        targetServers
                .stream()
                .filter( server -> message.getIgnoredTargets() == null || !message.getIgnoredTargets().contains( server ) )
                .map( server -> ProxyServer.getInstance().getServerInfo( server ) )
                .filter( Objects::nonNull )
                .forEach( server ->
                {
                    final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF( jsonMessage );

                    server.sendData( "bungeeutilisalsx:default-channel", out.toByteArray() );
                } );
    }

    @Override
    public void shutdownBridge()
    {
        consumersMap.clear();
        ProxyServer.getInstance().getPluginManager().unregisterListener( this );

        if ( eventHandlers != null )
        {
            eventHandlers.forEach( com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler::unregister );
            eventHandlers.clear();
        }
    }

    @EventHandler
    public void onPluginMessage( final PluginMessageEvent event )
    {
        if ( !event.getTag().equals( "bungeeutilisalsx:default-channel" ) )
        {
            return;
        }
        final ByteArrayDataInput input = ByteStreams.newDataInput( event.getData() );
        final BridgedMessage message = BUCore.getGson().fromJson( input.readUTF(), BridgedMessage.class );

        if ( !super.canAccept( message ) )
        {
            return;
        }

        final BridgeResponseEvent responseEvent = new BridgeResponseEvent(
                message.getType(),
                message.getIdentifier(),
                message.getFrom(),
                message.getAction(),
                message.getMessage()
        );
        BUCore.getApi().getEventLoader().launchEventAsync( responseEvent );
    }
}
