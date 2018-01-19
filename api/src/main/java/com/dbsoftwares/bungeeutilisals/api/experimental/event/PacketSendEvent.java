package com.dbsoftwares.bungeeutilisals.api.experimental.event;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.interfaces.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;

public class PacketSendEvent extends AbstractEvent implements Cancellable {

    private boolean cancelled;
    private DefinedPacket packet;
    private Connection sender;
    private Connection reciever;
    private ProxiedPlayer player;

    public PacketSendEvent(DefinedPacket packet, ProxiedPlayer p, Connection sender, Connection reciever) {
        this.cancelled = false;
        this.player = p;
        this.packet = packet;
        this.sender = sender;
        this.reciever = reciever;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public User getUser() {
        return BUCore.getApi().getUser(player).orElse(null);
    }

    public DefinedPacket getPacket() {
        return packet;
    }

    public Connection getSender() {
        return sender;
    }

    public Connection getReciever() {
        return reciever;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}