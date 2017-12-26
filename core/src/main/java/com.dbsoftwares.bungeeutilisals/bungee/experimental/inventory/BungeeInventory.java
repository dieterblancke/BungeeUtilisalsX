package com.dbsoftwares.bungeeutilisals.bungee.experimental.inventory;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.InventoryClickEvent;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.Inventory;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.InventoryType;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.Material;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class BungeeInventory implements Inventory {

    // TODO: Base inventory on BungeeUtil ?? Or make completely customized version.

    private HashMap<Integer, ItemStack> map;
    private InventoryType type;
    private String title;
    private int size;
    private int inventoryID;

    public BungeeInventory() {
        this(InventoryType.CHEST_INVENTORY, "Chest", 27);
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
        this.type = type;
        this.map = Maps.newHashMap();
        this.title = title;
        this.size = size;
        this.inventoryID = InventoryRegistry.getCurrentInventoryId().getAndIncrement();

        if (InventoryRegistry.getCurrentInventoryId().get() > 999) {
            InventoryRegistry.getCurrentInventoryId().set(0);
        }

        InventoryRegistry.getInventories().put(inventoryID, this);
    }

    public int getInventoryId() {
        return inventoryID;
    }

    public void setSize(int i) {
        this.size = i;
    }

    public int getSize() {
        return size;
    }

    public String getTitle() {
        return title;
    }

    public ItemStack getItem(int slot) {
        if (map.containsKey(slot)) {
            return map.get(slot);
        }
        return null;
    }

    public boolean setItem(int slot, ItemStack stack) {
        map.put(slot, stack);

        for (UUID id : InventoryRegistry.getOpen().keySet()) {
            if (InventoryRegistry.getOpen().get(id) == inventoryID) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(id);
                if (p != null) {
                    updateSlot(p, slot);
                }
            }
        }

        return true;
    }

    public boolean addItem(ItemStack stack) {
        int slot = 0;
        while (map.containsKey(slot)) {
            slot += 1;
        }

        return slot <= size - 1 && setItem(slot, stack);
    }

//    public boolean open(ProxiedPlayer p) {
//        InventoryRegistry.getOpen().put(p.getUniqueId(), inventoryID);
//
//        OutOpenWindow window = new OutOpenWindow();
//        window.id = inventoryID;
//        window.title = "{\"text\":\"" + title + "\"}";
//        window.slots = size;
//        window.windowType = "Chest";
//
//        p.unsafe().sendPacket(window);
//
//        for (int i : map.keySet()) {
//            OutSetSlot slot = new OutSetSlot();
//            slot.windowID = inventoryID;
//            slot.slot = i;
//            slot.item = map.get(i);
//
//            if (slot.item != null) {
//                p.unsafe().sendPacket(slot);
//            }
//        }
//
//        return true;
//    }

    @Override
    public void destroy() {
        List<UUID> remove = Lists.newArrayList();
        InventoryRegistry.getOpen().keySet().stream().filter(id -> InventoryRegistry.getOpen().get(id) == inventoryID).forEach(remove::add);
        remove.forEach(id -> InventoryRegistry.getOpen().remove(id));
        InventoryRegistry.getInventories().remove(inventoryID);
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

    private void handleClick(ProxiedPlayer p, ItemStack stack, int slot, boolean shift, boolean leftClick) {
        PlayerInventory pinv = InventoryRegistry.getPlayerInventory(p.getUniqueId());

        if (slot >= size) {
            // Click outside inventory window, not listening.
            return;
        }

        InventoryClickEvent event = new InventoryClickEvent(p, this, slot, stack, leftClick, shift);
        BUCore.getApi().getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            for (int i = 0; i < size; i++) {
                if (map.containsKey(i)) {
                    OutSetSlot slotPacket = new OutSetSlot();
                    slotPacket.windowID = inventoryID;
                    slotPacket.slot = i;
                    slotPacket.item = map.get(i);

                    p.unsafe().sendPacket(slotPacket);
                } else {
                    OutSetSlot slotPacket = new OutSetSlot();
                    slotPacket.windowID = inventoryID;
                    slotPacket.slot = i;
                    slotPacket.item = null;

                    p.unsafe().sendPacket(slotPacket);
                }
            }

            OutWindowItems pWindow = new OutWindowItems();
            pWindow.id = 0;
            pWindow.items = pinv.getItems();

            p.unsafe().sendPacket(pWindow);

            OutSetSlot nullifyPacket = new OutSetSlot();
            nullifyPacket.windowID = -1;
            nullifyPacket.slot = -1;

            p.unsafe().sendPacket(nullifyPacket);
        }
    }

    private void updateSlot(ProxiedPlayer p, int slot) {
        OutSetSlot packet = new OutSetSlot();
        packet.windowID = inventoryID;
        packet.slot = slot;
        packet.item = map.getOrDefault(slot, null);

        p.unsafe().sendPacket(packet);
    }
}