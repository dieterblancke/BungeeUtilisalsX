package com.dbsoftwares.bungeeutilisals.api.experimental.packets.client;

import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.Packet;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

@EqualsAndHashCode(callSuper = true)
@Data
public class PacketPlayInWindowClick extends Packet {

	public int windowID;
	public int slot;
	public int button;
	public short actionNumber;
	public ItemStack item;
	public int shift;

    public PacketPlayInWindowClick() {
        super(0x12);
    }

	@Override
	public void read(ByteBuf buf, Direction direction, int protocolVersion) {
		this.windowID = buf.readUnsignedByte();
		this.slot = buf.readShort();
		this.button = buf.readByte();
		this.actionNumber = buf.readShort();
		this.shift = buf.readByte();

		this.item = readItem(buf);
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

    @Override
    public void handle(User user) { }

    public static class Mode {

		public static final int NORMAL_LEFT_CLICK = getInt(0, 0);
		public static final int NORMAL_RIGHT_CLICK = getInt(0, 1);
		public static final int NORMAL_MIDDLE_CLICK = getInt(3, 2);
		public static final int NORMAL_DOUBLE_CLICK = getInt(6, 0);

		public static final int SHIFT_LEFT_CLICK = getInt(1, 0);
		public static final int SHIFT_RIGHT_CLICK = getInt(1, 1);

		public static final int KEY_1 = getInt(2, 0);
		public static final int KEY_2 = getInt(2, 1);
		public static final int KEY_3 = getInt(2, 2);
		public static final int KEY_4 = getInt(2, 3);
		public static final int KEY_5 = getInt(2, 4);
		public static final int KEY_6 = getInt(2, 5);
		public static final int KEY_7 = getInt(2, 6);
		public static final int KEY_8 = getInt(2, 7);
		public static final int KEY_9 = getInt(2, 8);

		public static final int DROP_ITEM = getInt(4, 0);
		public static final int DROP_ITEM_STACK = getInt(4, 1);

		public static final int DRAG_START_LEFT = getInt(5, 0);
		public static final int DRAG_ADD_LEFT = getInt(5, 1);
		public static final int DRAG_END_LEFT = getInt(5, 2);
		public static final int DRAG_START_RIGHT = getInt(5, 4);
		public static final int DRAG_ADD_RIGHT = getInt(5, 5);

		public static final int DRAG_END_RIGHT = getInt(5, 6);

		private static int getInt(int mode, int button) {
			return (mode << 4) + button;
		}

		private static int getMode(int mode) {
			return mode >> 4;
		}

		public static boolean inDrop(int mode) {
			return getMode(mode) == 4;
		}

		public static boolean isDrag(int mode) {
			return getMode(mode) == 5;
		}

		public static boolean isKey(int mode) {
			return getMode(mode) == 2;
		}

		public static boolean isNormalClick(int mode) {
			return getMode(mode) == 0 || getMode(mode) == 3 || getMode(mode) == 6;
		}

		public static boolean isShiftClick(int mode) {
			return getMode(mode) == 1;
		}
	}

	private short actionNumber;
	private Item item;
	private int mode = 0;
	private int slot;
	private int window;

	@Override
	public void read(PacketDataSerializer s) {
		this.window = s.readByte();
		this.slot = s.readShort();
		this.mode += s.readByte();
		this.actionNumber = s.readShort();
		this.mode += s.readByte() << 4;
		this.item = s.readItem();
	}

	@Override
	public String toString() {
		return "PacketPlayInWindowClick [actionNumber=" + this.actionNumber + ", item=" + this.item + ", shift=" + this.mode + ", slot=" + this.slot + ", window=" + this.window + "]";
	}

	@Override
	public void write(PacketDataSerializer s) {
		s.writeByte(this.window);
		s.writeShort(this.slot);
		s.writeByte(this.mode & 0x0F);
		s.writeShort(this.actionNumber);
		s.writeByte(this.mode >> 4);
		s.writeItem(this.item);
	}
}
}