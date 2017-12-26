package com.dbsoftwares.bungeeutilisals.api.experimental.packets.client;

import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

public class InWindowClick extends StructurePacket {

	public int windowID;
	public int slot;
	public int button;
	public short actionNumber;
	public ItemStack item;
	public int shift;

	@Override
	public void read(ByteBuf buf, Direction direction, int protocolVersion) {
		this.windowID = buf.readUnsignedByte();
		this.slot = buf.readShort();
		this.button = buf.readByte();
		this.actionNumber = buf.readShort();
		this.shift = buf.readByte();

		this.item = new ItemStack(buf);
	}

	@Override
	public void write(ByteBuf buf, Direction direction, int protocolVersion) {
		buf.writeByte(windowID);
		buf.writeShort(slot);
		buf.writeByte(button);
		buf.writeShort(actionNumber);
		buf.writeByte(shift);

		item.write(buf);
	}
}