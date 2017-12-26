package com.dbsoftwares.bungeeutilisals.api.experimental.encoder;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.experimental.connection.BungeeConnection;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.OnPacketEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.PacketWrapper;

import java.util.List;

public class CustomDecoder extends MessageToMessageDecoder<PacketWrapper> {

    private boolean server;
    private ProxiedPlayer p;

    public CustomDecoder(boolean server, ProxiedPlayer player) {
        this.server = server;
        this.p = player;
    }

    @Override
    protected void decode(ChannelHandlerContext chx, PacketWrapper wrapper, List<Object> out) {
        if (wrapper.packet == null) {
            out.add(wrapper);
            return;
        }

        OnPacketEvent event = null;

        if (server) {
            if (p instanceof UserConnection) {
                UserConnection u = (UserConnection) p;
                event = new OnPacketEvent(wrapper.packet, p, u.getServer(), new BungeeConnection());
            }
        } else {
            event = new OnPacketEvent(wrapper.packet, p, p, new BungeeConnection());
        }

        if (event != null) {
            BUCore.getApi().getEventLoader().launchEvent(event);
        }

        boolean cancelled = false;
        if (event != null) {
            cancelled = event.isCancelled();
        }

        if (!cancelled) {
            out.add(wrapper);
        }
    }
}