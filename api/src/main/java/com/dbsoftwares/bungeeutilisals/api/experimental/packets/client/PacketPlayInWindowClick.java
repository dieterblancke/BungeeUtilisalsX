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

    public int window;
    private short actionNumber;
    private ItemStack item;
    private int mode = 0;
    private int slot;

    public PacketPlayInWindowClick() {
        super(0x12);
    }

    @Override
    public void read(ByteBuf buf, Direction direction, int protocolVersion) {
        this.window = buf.readUnsignedByte();
        this.slot = buf.readShort();
        this.mode += buf.readByte();
        this.actionNumber = buf.readShort();
        this.mode += buf.readByte() << 4;
        this.item = readItem(buf);
    }

    @Override
    public void write(ByteBuf buf, Direction direction, int protocolVersion) {
        buf.writeByte(window);
        buf.writeShort(slot);
        buf.writeByte(mode & 0x0F);
        buf.writeShort(actionNumber);
        buf.writeByte(mode >> 4);
        writeItem(buf, item);
    }

    @Override
    public void handle(User user) {
    }

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