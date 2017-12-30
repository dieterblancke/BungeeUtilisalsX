package com.dbsoftwares.bungeeutilisals.bungee.experimental.inventory;

import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.Inventory;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.InventoryType;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.InventoryUnsafe;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.Material;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class BungeeInventory implements Inventory {

    private TreeMap<Integer, ItemStack> map;
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
        this.map = Maps.newTreeMap();
        this.title = title;
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int i) {
        this.size = i;
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
        if (stack == null || stack.getType().equals(Material.AIR)) {
            map.remove(slot);
        }
        map.put(slot, stack);
        return true;
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
        unsafe.getViewers().clear();
        map.clear();
        size = type.getDefaultSlots();
        type = null;
        title = null;
    }

    @Override
    public TreeMap<Integer, ItemStack> addItem(ItemStack... items) {
        TreeMap<Integer, ItemStack> leftover = Maps.newTreeMap();

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
    public TreeMap<Integer, ItemStack> removeItem(ItemStack... items) {
        TreeMap<Integer, ItemStack> leftover = new TreeMap<>();

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            int toDelete = item.getAmount();

            while (true) {
                int first = first(item);
                if (first == -1) {
                    item.setAmount(toDelete);
                    leftover.put(i, item);
                    break;
                } else {
                    ItemStack itemStack = getItem(first);
                    int amount = itemStack.getAmount();

                    if (amount <= toDelete) {
                        toDelete -= amount;
                        clear(first);
                    } else {
                        itemStack.setAmount(amount - toDelete);
                        setItem(first, itemStack);
                        toDelete = 0;
                    }
                }
                if (toDelete <= 0) {
                    break;
                }
            }
        }
        return leftover;
    }

    @Override
    public TreeMap<Integer, ItemStack> getContents() {
        return map;
    }

    @Override
    public void setContents(TreeMap<Integer, ItemStack> items) {
        this.map = items;
    }

    @Override
    public boolean contains(Material mat) {
        for (ItemStack item : map.values()) {
            if (item.getType().equals(mat)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(ItemStack item) {
        return containsAtLeast(item, item.getAmount());
    }

    @Override
    public boolean containsAtLeast(Material mat, int amount) {
        int amountOfSimilar = 0;
        for (ItemStack item : map.values()) {
            if (item.getType().equals(mat)) {
                amountOfSimilar += item.getAmount();
            }
        }
        return amountOfSimilar >= amount;
    }

    @Override
    public boolean containsAtLeast(ItemStack item, int amount) {
        int amountOfSimilar = 0;
        for (ItemStack i : map.values()) {
            if (i.isSimilarTo(item)) {
                amountOfSimilar += i.getAmount();
            }
        }
        return amountOfSimilar >= amount;
    }

    @Override
    public TreeMap<Integer, ItemStack> all(Material mat) {
        TreeMap<Integer, ItemStack> items = Maps.newTreeMap();

        for (Map.Entry<Integer, ItemStack> entry : map.entrySet()) {
            if (entry.getValue().getType().equals(mat)) {
                items.put(entry.getKey(), entry.getValue());
            }
        }
        return items;
    }

    @Override
    public TreeMap<Integer, ItemStack> all(ItemStack item) {
        TreeMap<Integer, ItemStack> items = Maps.newTreeMap();

        for (Map.Entry<Integer, ItemStack> entry : map.entrySet()) {
            if (entry.getValue().isSimilarTo(item)) {
                items.put(entry.getKey(), entry.getValue());
            }
        }
        return items;
    }

    @Override
    public int first(Material mat) {
        for (Map.Entry<Integer, ItemStack> entry : map.entrySet()) {
            if (entry.getValue().getType().equals(mat)) {
                return entry.getKey();
            }
        }
        return 0;
    }

    @Override
    public int first(ItemStack item) {
        for (Map.Entry<Integer, ItemStack> entry : map.entrySet()) {
            if (entry.getValue().isSimilarTo(item)) {
                return entry.getKey();
            }
        }
        return 0;
    }

    @Override
    public int firstEmpty() {
        for (int i = 0; i < size; i++) {
            if (!map.containsKey(i) || map.get(i).getType().equals(Material.AIR)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void remove(Material mat) {
        TreeMap<Integer, ItemStack> items = all(mat);

        for (int slot : items.keySet()) {
            setItem(slot, null);
        }
    }

    @Override
    public void remove(ItemStack item) {

    }

    @Override
    public void clear(int slot) {

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
        return type;
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
        LinkedList<ItemStack> inventory = Lists.newLinkedList(map.values());
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack item = inventory.get(i);
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
        LinkedList<ItemStack> inventory = Lists.newLinkedList(map.values());
        ItemStack filteredItem = (ItemStack) item.clone();
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack cItem = inventory.get(i);
            if (cItem != null && cItem.getAmount() <= filteredItem.getAmount() && cItem.isSimilarTo(filteredItem)) {
                return i;
            }
        }
        return -1;
    }
}