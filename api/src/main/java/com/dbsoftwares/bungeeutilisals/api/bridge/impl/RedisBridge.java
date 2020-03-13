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
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Consumer;

public class RedisBridge implements Bridge, Listener
{

    private static final Gson gson = new Gson();
    private Map<String, AbstractMap.SimpleEntry<Class<?>, Consumer<?>>> map = Maps.newConcurrentMap();

    @Override
    public boolean setup()
    {
        // TODO: check redis option in config
        if ( !FileLocation.CONFIG.getConfiguration().getBoolean( "redis" ) )
        {
            return false;
        }
        try
        {
            Class.forName( "com.imaginarycode.minecraft.redisbungee.RedisBungee" );
        }
        catch ( ClassNotFoundException e )
        {
            return false;
        }

        getApi().registerPubSubChannels( "BUX_DEFAULT_CHANNEL" );
        return true;
    }

    @Override
    public void sendMessage( Object data )
    {
        // TODO
    }

    @Override
    public <T> void sendMessage( Object data, Class<T> type, Consumer<T> consumer )
    {
        // TODO: create Object to be stringified by Gson and sent over channel.
    }

    @EventHandler
    public void onPubSubMessage( final com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent event )
    {
        if ( !event.getChannel().equalsIgnoreCase( "BUX_DEFAULT_CHANNEL" ) )
        {
            return;
        }
        final BridgedMessage message = gson.fromJson( event.getMessage(), BridgedMessage.class );

        final BridgeResponseEvent responseEvent = new BridgeResponseEvent(
                BridgeType.BUNGEE_BUNGEE, message.getIdentifier(), message.getFrom(), message.getMessage()
        );
        BUCore.getApi().getEventLoader().launchEventAsync( responseEvent );
    }

    private com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI getApi()
    {
        return com.imaginarycode.minecraft.redisbungee.RedisBungee.getApi();
    }
}
