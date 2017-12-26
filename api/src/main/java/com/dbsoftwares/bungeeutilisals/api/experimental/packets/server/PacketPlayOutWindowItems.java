package com.dbsoftwares.bungeeutilisals.api.experimental.packets.server;

import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.Packet;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.protocol.ProtocolConstants;

@EqualsAndHashCode(callSuper = true)
@Data
public class PacketPlayOutWindowItems extends Packet {

    private int window;
    private ItemStack[] items;

    public PacketPlayOutWindowItems(int window, ItemStack[] items) {
        super(0x14);

        this.window = window;
        this.items = items;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.window = buf.readUnsignedByte();
        short items_length = buf.readShort();

        this.items = new ItemStack[items_length];

        for (int i = 0; i < items_length; i++) {
            this.items[i] = readItem(buf);
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        buf.writeByte(window);
        buf.writeShort(items.length);

        for (ItemStack item : items) {
            writeItem(buf, item);
        }
    }

    @Override
    public void handle(User user) { }
}