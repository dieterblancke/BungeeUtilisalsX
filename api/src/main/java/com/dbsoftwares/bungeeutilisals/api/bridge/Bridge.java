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

package com.dbsoftwares.bungeeutilisals.api.bridge;

import com.dbsoftwares.bungeeutilisals.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisals.api.bridge.message.BridgedMessage;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Bridge implements EventExecutor
{

    @Getter
    protected boolean setup;
    protected Map<String, SimpleEntry<Class<?>, Consumer>> consumersMap = Maps.newConcurrentMap();

    protected boolean canAccept( final BridgedMessage message )
    {
        final String currentName = FileLocation.CONFIG.getConfiguration().getString( "bridging.name" );

        if ( message.getTargets() != null && !message.getTargets().isEmpty() )
        {
            if ( !message.getTargets().contains( currentName ) )
            {
                return false;
            }
        }

        if ( message.getIgnoredTargets() != null && !message.getIgnoredTargets().isEmpty() )
        {
            return !message.getIgnoredTargets().contains( currentName );
        }
        return true;
    }

    @Event
    public void onBridgeResponse( final BridgeResponseEvent event )
    {
        final String identifier = event.getIdentifier().toString();
        if ( !consumersMap.containsKey( identifier ) )
        {
            return;
        }
        final SimpleEntry<Class<?>, Consumer> entry = consumersMap.remove( identifier );

        entry.getValue().accept( event.asCasted( entry.getKey() ) );
    }

    public abstract boolean setup();

    public abstract BridgedMessage sendMessage( final BridgeType type, final String action, final Object data );

    public abstract <T> BridgedMessage sendMessage(
            final BridgeType type,
            final String action,
            final Object data,
            final Class<T> responseType,
            final Consumer<T> consumer
    );

    public abstract BridgedMessage sendTargetedMessage(
            final BridgeType type,
            final List<String> targets,
            final List<String> ignoredTargets,
            final String action,
            final Object data
    );

    public abstract <T> BridgedMessage sendTargetedMessage(
            final BridgeType type,
            final List<String> targets,
            final List<String> ignoredTargets,
            final String action,
            final Object data,
            final Class<T> responseType,
            final Consumer<T> consumer
    );

    public abstract void shutdownBridge();
}
