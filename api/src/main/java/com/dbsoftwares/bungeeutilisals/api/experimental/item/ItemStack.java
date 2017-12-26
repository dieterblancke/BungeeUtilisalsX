package com.dbsoftwares.bungeeutilisals.api.experimental.item;

import com.dbsoftwares.bungeeutilisals.api.experimental.packets.nbt.NBTCompressedStreamTools;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.nbt.NBTReadLimiter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.Getter;
import lombok.Setter;

import java.io.DataOutput;

public class ItemStack implements Cloneable {

    @Getter private Material type;
    @Getter private int amount;
    @Getter private int data;

    @Getter @Setter private ItemMeta itemMeta;

    public ItemStack(Material type) {
        this.type = type;
        this.amount = 1;
        this.data = 0;
    }

    public ItemStack(ByteBuf buf) {
        short id = buf.readShort();

        if (id >= 0) {
            int amount = buf.readByte();
            int data = buf.readShort();

            this.type = Material.getMaterial(id);

            if (this.type == null) {
                System.out.println("Null type for id: " + id);
            }

            this.amount = amount;
            this.data = data;

            int i = buf.readerIndex();
            byte b = buf.readByte();

            if (b == 0) {
                return;
            }

            buf.readerIndex(i);
            this.itemMeta.fromTag(NBTCompressedStreamTools.a(new ByteBufInputStream(buf), new NBTReadLimiter(2097152L)));
        }
    }

    public void write(ByteBuf buf) {
        if (type == null)
            buf.writeShort(-1);
        else {
            buf.writeShort(type.getId());
            buf.writeByte(amount);
            buf.writeShort(data);

            if (itemMeta.getTag() == null || itemMeta.getTag().isEmpty())
                buf.writeByte(0);
            else {
                NBTCompressedStreamTools.a(itemMeta.getTag(), (DataOutput) new ByteBufOutputStream(buf));
            }
        }
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ItemStack setType(Material type) {
        this.type = type;
        return this;
    }

    public ItemStack setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStack setData(int data) {
        this.data = data;
        return this;
    }

    public boolean isSimilarTo(ItemStack item) {
        if (item == null) {
            return false;
        } else if (item == this) {
            return true;
        } else {
            if (item.getType().getId() == this.getType().getId() && this.getType().getMaxDurability() == item.getType().getMaxDurability()) {
                return this.getItemMeta().isSimilar(item.getItemMeta());
            } else {
                return false;
            }
        }
    }
}