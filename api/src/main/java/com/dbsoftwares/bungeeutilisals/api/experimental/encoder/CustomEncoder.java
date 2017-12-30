package com.dbsoftwares.bungeeutilisals.api.experimental.encoder;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.experimental.connection.BungeeConnection;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.PacketUpdateEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.List;

public class CustomEncoder extends MessageToMessageEncoder<DefinedPacket> {

    private boolean server;
    private ProxiedPlayer p;

    public CustomEncoder(boolean server, ProxiedPlayer p) {
        this.server = server;
        this.p = p;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, DefinedPacket msg, List<Object> out) {
        PacketUpdateEvent event = null;

        if (server) {
            event = new PacketUpdateEvent(msg, p, new BungeeConnection(), p);
        } else {
            if (p instanceof UserConnection) {
                UserConnection u = (UserConnection) p;
                event = new PacketUpdateEvent(msg, p, new BungeeConnection(), u.getServer());
            }
        }

        if (event != null) {
            BUCore.getApi().getEventLoader().launchEvent(event);
        }

        boolean cancelled = false;
        if (event != null) {
            cancelled = event.isCancelled();
        }

        if (!cancelled) {
            out.add(msg);
        }
    }
}