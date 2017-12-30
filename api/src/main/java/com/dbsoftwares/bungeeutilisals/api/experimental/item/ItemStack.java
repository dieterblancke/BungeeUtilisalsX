package com.dbsoftwares.bungeeutilisals.api.experimental.item;

import lombok.Getter;
import lombok.Setter;

public class ItemStack implements Cloneable {

    @Getter private Material type;
    @Getter private int amount;
    @Getter private int data;

    @Getter @Setter private ItemMeta itemMeta = new ItemMeta();

    public ItemStack(Material type) {
        this.type = type;
        this.amount = 1;
        this.data = 0;
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