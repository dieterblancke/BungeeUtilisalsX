package com.dbsoftwares.bungeeutilisals.api.experimental.packets.server;

import com.dbsoftwares.bungeeutilisals.api.experimental.packets.Packet;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

@Data
public class PacketPlayOutOpenWindow extends Packet {

    private boolean horse;
    private int id;
    private String title;
    private int slots;
    private String type;

    public PacketPlayOutOpenWindow() {
        super(0x13);
    }

    public PacketPlayOutOpenWindow(int id, String type, String title, int slots, boolean ishorse) {
        super(0x13);
        this.id = id;
        this.type = type;
        this.title = title;
        this.slots = slots;
        this.horse = ishorse;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        id = buf.readByte();
        type = DefinedPacket.readString(buf);
        title = DefinedPacket.readString(buf);
        slots = buf.readUnsignedByte();
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        buf.writeByte(id);
        DefinedPacket.writeString(type, buf);
        if (!title.startsWith("{")) {
            if (!title.startsWith("\"")) {
                title = "\"" + title + "\"";
            }
            title = "{\"translate\":" + title + "}";
        }
        DefinedPacket.writeString(title, buf);
        buf.writeByte(slots);
    }

    @Override
    public void handle(User user) { }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PacketPlayOutOpenWindow)) {
            return false;
        }
        PacketPlayOutOpenWindow packet = (PacketPlayOutOpenWindow) other;
        return packet.horse = horse && packet.id == id && packet.title.equals(title) && packet.slots == slots && packet.type.equals(type);
    }
}