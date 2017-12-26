package com.dbsoftwares.bungeeutilisals.api.experimental.packets.client;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

public class InCloseWindow extends StructurePacket {
	
	public int windowID;
	
	public InCloseWindow(){}
	
	public InCloseWindow(int id) {
		this.windowID = id;
	}

	@Override
	public void read(ByteBuf buf, Direction direction, int protocolVersion) {
		this.windowID = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf, Direction direction, int protocolVersion) {
		buf.writeByte(windowID);
	}
}