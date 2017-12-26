package com.dbsoftwares.bungeeutilisals.api.experimental.packets;

/*
 * Created by DBSoftwares on 26 december 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.Material;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.nbt.NBTCompressedStreamTools;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.nbt.NBTReadLimiter;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import java.io.DataOutput;
import java.util.Optional;

public abstract class Packet extends DefinedPacket {

    int id;

    public Packet(int id) {
        this.id = id;
    }

    @Override
    public abstract void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion);

    @Override
    public abstract void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion);

    public abstract void handle(User user) throws Exception;

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        UserConnection connection = Utils.getConnection(handler);
        Optional<User> optionalUser = BUCore.getApi().getUser(connection.getName());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            handle(user);
        }
    }

    public static ItemStack readItem(ByteBuf buf) {
        short id = buf.readShort();

        if (id >= 0) {
            ItemStack item = new ItemStack(Material.getMaterial(id));
            item.setAmount(buf.readByte());
            item.setData(buf.readShort());

            int i = buf.readerIndex();
            byte b = buf.readByte();

            if (b == 0) {
                return item;
            }

            buf.readerIndex(i);
            item.getItemMeta().fromTag(NBTCompressedStreamTools.a(new ByteBufInputStream(buf), new NBTReadLimiter(2097152L)));

            return item;
        }
        return new ItemStack(Material.AIR);
    }

    public static void writeItem(ByteBuf buf, ItemStack item) {
        if (item.getType() == null) {
            buf.writeShort(-1);
        } else {
            buf.writeShort(item.getType().getId());
            buf.writeByte(item.getAmount());
            buf.writeShort(item.getData());

            if (item.getItemMeta().getTag() == null || item.getItemMeta().getTag().isEmpty()) {
                buf.writeByte(0);
            } else {
                NBTCompressedStreamTools.a(item.getItemMeta().getTag(), (DataOutput) new ByteBufOutputStream(buf));
            }
        }
    }
}