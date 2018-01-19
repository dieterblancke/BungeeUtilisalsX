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

public class BUEncoder extends MessageToMessageEncoder<DefinedPacket> {

    private boolean server;
    private ProxiedPlayer p;

    public BUEncoder(boolean server, ProxiedPlayer p) {
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

        if (event == null) {
            out.add(msg);
            return;
        }

        BUCore.getApi().getEventLoader().launchEvent(event);
        if (!event.isCancelled()) {
            out.add(msg);
        }
    }
}