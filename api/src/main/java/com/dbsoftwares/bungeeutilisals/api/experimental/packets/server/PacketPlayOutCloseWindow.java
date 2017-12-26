package com.dbsoftwares.bungeeutilisals.api.experimental.packets.server;

import com.dbsoftwares.bungeeutilisals.api.experimental.packets.Packet;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

@EqualsAndHashCode(callSuper = true)
@Data
public class PacketPlayOutCloseWindow extends Packet {

    private int window;

    public PacketPlayOutCloseWindow(int window) {
        super(0x08);
        this.window = window;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.window = buf.readUnsignedByte();
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        buf.writeByte(window);
    }

    @Override
    public void handle(User user) { }
}