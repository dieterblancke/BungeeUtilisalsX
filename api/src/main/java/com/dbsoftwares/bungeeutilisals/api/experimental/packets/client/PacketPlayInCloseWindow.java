package com.dbsoftwares.bungeeutilisals.api.experimental.packets.client;

import com.dbsoftwares.bungeeutilisals.api.experimental.packets.Packet;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

@EqualsAndHashCode(callSuper = true)
@Data
public class PacketPlayInCloseWindow extends Packet {
	
	public int window;

	public PacketPlayInCloseWindow() {
		super(0x12);
	}

	@Override
	public void read(ByteBuf buf, Direction direction, int protocolVersion) {
		this.window = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf, Direction direction, int protocolVersion) {
		buf.writeByte(window);
	}

	@Override
	public void handle(User user) { }
}