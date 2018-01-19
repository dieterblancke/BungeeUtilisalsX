package com.dbsoftwares.bungeeutilisals.api.experimental.encoder;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.experimental.connection.BungeeConnection;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.PacketUpdateEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.PacketWrapper;

import java.util.List;

public class BUDecoder extends MessageToMessageDecoder<PacketWrapper> {

    private boolean server;
    private ProxiedPlayer p;

    public BUDecoder(boolean server, ProxiedPlayer player) {
        this.server = server;
        this.p = player;
    }

    @Override
    protected void decode(ChannelHandlerContext chx, PacketWrapper wrapper, List<Object> out) {
        if (wrapper.packet == null) {
            out.add(wrapper);
            return;
        }

        PacketUpdateEvent event = null;
        if (server) {
            if (p instanceof UserConnection) {
                UserConnection u = (UserConnection) p;
                event = new PacketUpdateEvent(wrapper.packet, p, u.getServer(), new BungeeConnection());
            }
        } else {
            event = new PacketUpdateEvent(wrapper.packet, p, p, new BungeeConnection());
        }

        if (event == null) {
            out.add(wrapper);
            return;
        }

        BUCore.getApi().getEventLoader().launchEvent(event);
        if (!event.isCancelled()) {
            out.add(wrapper);
        }
    }
}