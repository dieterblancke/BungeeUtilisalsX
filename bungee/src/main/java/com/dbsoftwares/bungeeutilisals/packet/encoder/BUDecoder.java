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

package com.dbsoftwares.bungeeutilisals.packet.encoder;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.packet.connection.BungeeConnection;
import com.dbsoftwares.bungeeutilisals.packet.event.PacketUpdateEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.PacketWrapper;

import java.util.List;

public class BUDecoder extends MessageToMessageDecoder<PacketWrapper>
{

    private boolean server;
    private ProxiedPlayer p;

    public BUDecoder( boolean server, ProxiedPlayer player )
    {
        this.server = server;
        this.p = player;
    }

    @Override
    protected void decode( ChannelHandlerContext context, PacketWrapper wrapper, List<Object> output )
    {
        if ( wrapper.packet == null )
        {
            output.add( wrapper );
            return;
        }

        PacketUpdateEvent event;
        if ( server )
        {
            event = new PacketUpdateEvent( wrapper.packet, p, p.getServer(), new BungeeConnection() );
        } else
        {
            event = new PacketUpdateEvent( wrapper.packet, p, p, new BungeeConnection() );
        }

        BUCore.getApi().getEventLoader().launchEvent( event );
        if ( !event.isCancelled() )
        {
            output.add( wrapper );
        }
    }
}