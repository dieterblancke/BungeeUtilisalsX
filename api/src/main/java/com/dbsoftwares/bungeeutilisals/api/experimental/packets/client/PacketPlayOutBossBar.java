package com.dbsoftwares.bungeeutilisals.api.experimental.packets.client;

/*
 * Created by DBSoftwares on 15/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BossBarAction;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.Packet;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.Arrays;
import java.util.UUID;

@Getter
public class PacketPlayOutBossBar extends Packet {

    private UUID barId;
    private BossBarAction action;
    private BaseComponent[] title;
    private float health;
    private BarColor color;
    private BarStyle style;
    private short flags;

    public PacketPlayOutBossBar() {
        super(0x0C);
    }

    public PacketPlayOutBossBar(UUID id, BossBarAction action, BaseComponent[] title) {
        super(0x0C);
        this.barId = id;
        this.action = action;
        this.title = title;
    }

    public PacketPlayOutBossBar(UUID id, BossBarAction action, float health) {
        super(0x0C);
        this.barId = id;
        this.action = action;
        this.health = health;
    }

    public PacketPlayOutBossBar(UUID id, BossBarAction action, BarColor color, BarStyle style) {
        super(0x0C);
        this.barId = id;
        this.action = action;
        this.color = color;
        this.style = style;
    }

    public PacketPlayOutBossBar(UUID id, BossBarAction action, short flags) {
        super(0x0C);
        this.barId = id;
        this.action = action;
        this.flags = flags;
    }

    public PacketPlayOutBossBar(UUID id, BossBarAction action) {
        super(0x0C);
        this.barId = id;
        this.action = action;
    }

    public PacketPlayOutBossBar(UUID id, BossBarAction action, BaseComponent[] title, float health, BarColor color, BarStyle style) {
        super(0x0C);
        this.barId = id;
        this.action = action;
        this.title = title;
        this.health = health;
        this.color = color;
        this.style = style;
    }

    public PacketPlayOutBossBar(UUID id, BossBarAction action, BaseComponent[] title, float health, BarColor color, BarStyle style, short flags) {
        super(0x0C);
        this.barId = id;
        this.action = action;
        this.title = title;
        this.health = health;
        this.color = color;
        this.style = style;
        this.flags = flags;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.barId = readUUID(buf);
        this.action = BossBarAction.fromId(readVarInt(buf));

        if (this.action.equals(BossBarAction.ADD)) {
            this.title = ComponentSerializer.parse(readString(buf));
            this.health = buf.readFloat();
            this.color = BarColor.fromId(readVarInt(buf));
            this.style = BarStyle.fromId(readVarInt(buf));
            this.flags = buf.readUnsignedByte();
        } else if (this.action.equals(BossBarAction.UPDATE_HEALTH)) {
            this.health = buf.readFloat();
        } else if (this.action.equals(BossBarAction.UPDATE_TITLE)) {
            this.title = ComponentSerializer.parse(readString(buf));
        } else if (this.action.equals(BossBarAction.UPDATE_STYLE)) {
            this.color = BarColor.fromId(readVarInt(buf));
            this.style = BarStyle.fromId(readVarInt(buf));
        } else if (this.action.equals(BossBarAction.UPDATE_FLAGS)) {
            this.flags = buf.readUnsignedByte();
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        writeUUID(barId, buf);
        writeVarInt(action.getId(), buf);

        switch (action.getId()) {
            case 0:
                writeString(ComponentSerializer.toString(title), buf);
                buf.writeFloat(health);
                writeVarInt(color.getId(), buf);
                writeVarInt(style.getId(), buf);
                buf.writeByte(flags);
                break;
            case 2:
                buf.writeFloat(health);
                break;
            case 3:
                writeString(ComponentSerializer.toString(title), buf);
                break;
            case 4:
                writeVarInt(color.getId(), buf);
                writeVarInt(style.getId(), buf);
                break;
            case 5:
                buf.writeByte(flags);
                break;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PacketPlayOutBossBar)) {
            return false;
        }
        PacketPlayOutBossBar packet = (PacketPlayOutBossBar) other;
        return packet.barId.equals(barId) && packet.action.equals(action) && Arrays.equals(packet.title, title)
                && packet.health == health && packet.color.equals(color) && packet.style.equals(style) && packet.flags == flags;
    }

    @Override
    public String toString() {
        return String.format("PacketPlayOutBossBar [barId = %s, action = %s, title = %s, health = %s, color = %s, style = %s, flags = %s ]",
                barId, action.getId(), title == null ? "" : title, health, color == null ? "" : color.getId(),
                style == null ? "" : style.getId(), flags);
    }

    @Override
    public void handle(User user) { }
}