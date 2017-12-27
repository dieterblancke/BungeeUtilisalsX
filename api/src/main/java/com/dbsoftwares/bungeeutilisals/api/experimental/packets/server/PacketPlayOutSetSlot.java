package com.dbsoftwares.bungeeutilisals.api.experimental.packets.server;

/*
 * Created by DBSoftwares on 26 december 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.Packet;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.protocol.ProtocolConstants;

@EqualsAndHashCode(callSuper = true)
@Data
public class PacketPlayOutSetSlot extends Packet {

    private ItemStack item;
    private int window;
    private int slot;

    public PacketPlayOutSetSlot() {
        super(0x16);
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.window = buf.readByte();
        this.slot = buf.readShort();
        this.item = readItem(buf);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        buf.writeByte(window);
        buf.writeShort(slot);
        writeItem(buf, item);
    }

    @Override
    public void handle(User user) { }
}