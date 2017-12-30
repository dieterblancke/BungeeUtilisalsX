package com.dbsoftwares.bungeeutilisals.bungee.experimental.inventory;

import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.Inventory;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.InventoryType;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.InventoryUnsafe;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.Material;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class BungeeInventory implements Inventory {

    private HashMap<Integer, ItemStack> map;
    private InventoryType type;
    private String title;
    private int size;
    private InventoryUnsafe unsafe;

    public BungeeInventory() {
        this(InventoryType.CHEST, "Chest", 27);
    }

    public BungeeInventory(InventoryType type) {
        this(type, "Chest", 27);
    }

    public BungeeInventory(InventoryType type, String title, int size) {
        if(size <= 0) {
            throw new RuntimeException(size + " must cant be smaler or equal to zero");
        }
        if(size % 9 != 0) {
            throw new RuntimeException(size + " % 9 != 0");
        }
        this.unsafe = new InventoryUnsafe();
        this.type = type;
        this.map = Maps.newHashMap();
        this.title = title;
        this.size = size;
    }

    @Override
    public void setSize(int i) {

    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public ItemStack getItem(int slot) {
        return map.getOrDefault(slot, null);
    }

    @Override
    public boolean setItem(int slot, ItemStack stack) {
        return false;
    }

    public boolean addItem(ItemStack stack) {
        int slot = 0;
        while (map.containsKey(slot)) {
            slot += 1;
        }

        return slot <= size - 1 && setItem(slot, stack);
    }

    @Override
    public void destroy() {

    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        HashMap<Integer, ItemStack> leftover = Maps.newHashMap();

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            while (true) {
                int firstPartial = firstPartial(item);

                if (firstPartial == -1) {
                    int firstFree = firstEmpty();

                    if (firstFree == -1) {
                        leftover.put(i, item);
                        break;
                    } else {
                        if (item.getAmount() > 64) {
                            ItemStack stack = (ItemStack) item.clone();
                            stack.setAmount(64);
                            setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - 64);
                        } else {
                            setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    ItemStack partialItem = getItem(firstPartial);

                    int amount = item.getAmount();
                    int partialAmount = partialItem.getAmount();
                    int maxAmount = partialItem.getType().getMaxStackSize();

                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        setItem(firstPartial, partialItem);
                        break;
                    }

                    partialItem.setAmount(maxAmount);
                    setItem(firstPartial, partialItem);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        return leftover;
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... var1) {
        return null;
    }

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[0];
    }

    @Override
    public void setContents(ItemStack[] var1) {

    }

    @Override
    public ItemStack[] getStorageContents() {
        return new ItemStack[0];
    }

    @Override
    public void setStorageContents(ItemStack[] var1) {

    }

    @Override
    public boolean contains(Material var1) {
        return false;
    }

    @Override
    public boolean contains(ItemStack var1) {
        return false;
    }

    @Override
    public boolean contains(Material var1, int var2) {
        return false;
    }

    @Override
    public boolean contains(ItemStack var1, int var2) {
        return false;
    }

    @Override
    public boolean containsAtLeast(ItemStack var1, int var2) {
        return false;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material var1) {
        return null;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack var1) {
        return null;
    }

    @Override
    public int first(Material var1) {
        return 0;
    }

    @Override
    public int first(ItemStack var1) {
        return 0;
    }

    @Override
    public int firstEmpty() {
        return 0;
    }

    @Override
    public void remove(Material var1) {

    }

    @Override
    public void remove(ItemStack var1) {

    }

    @Override
    public void clear(int var1) {

    }

    @Override
    public void clear() {

    }

    @Override
    public List<ProxiedPlayer> getViewers() {
        return null;
    }

    @Override
    public InventoryType getType() {
        return null;
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return null;
    }

    @Override
    public ListIterator<ItemStack> iterator(int var1) {
        return null;
    }

    @Override
    public InventoryUnsafe unsafe() {
        return unsafe;
    }

    public int firstPartial(int materialId) {
        ItemStack[] inventory = getStorageContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.getType().getId() == materialId && item.getAmount() < item.getType().getMaxStackSize()) {
                return i;
            }
        }
        return -1;
    }

    public int firstPartial(Material material) {
        return firstPartial(material.getId());
    }

    private int firstPartial(ItemStack item) {
        ItemStack[] inventory = getStorageContents();
        ItemStack filteredItem = (ItemStack) item.clone();
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < inventory.length; i++) {
            ItemStack cItem = inventory[i];
            if (cItem != null && cItem.getAmount() < cItem.getType().getMaxStackSize() && cItem.isSimilarTo(filteredItem)) {
                return i;
            }
        }
        return -1;
    }
}