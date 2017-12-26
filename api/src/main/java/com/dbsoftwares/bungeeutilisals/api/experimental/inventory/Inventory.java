package com.dbsoftwares.bungeeutilisals.api.experimental.inventory;

import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.Material;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public interface Inventory {

    int getInventoryId();

    void setSize(int i);

    int getSize();

    String getTitle();

    ItemStack getItem(int slot);

    boolean setItem(int slot, ItemStack stack);

    boolean addItem(ItemStack stack);

    void destroy();

    HashMap<Integer, ItemStack> addItem(ItemStack... items);

    HashMap<Integer, ItemStack> removeItem(ItemStack... items);

    ItemStack[] getContents();

    void setContents(ItemStack[] items);

    ItemStack[] getStorageContents();

    void setStorageContents(ItemStack[] items);

    boolean contains(Material material);

    boolean contains(ItemStack item);

    boolean contains(Material material, int amount);

    boolean contains(ItemStack item, int amount);

    boolean containsAtLeast(ItemStack item, int amount);

    HashMap<Integer, ? extends ItemStack> all(Material material);

    HashMap<Integer, ? extends ItemStack> all(ItemStack item);

    int first(Material material);

    int first(ItemStack item);

    int firstEmpty();

    void remove(Material material);

    void remove(ItemStack item);

    void clear(int amount);

    void clear();

    List<ProxiedPlayer> getViewers();

    InventoryType getType();

    ListIterator<ItemStack> iterator();

    ListIterator<ItemStack> iterator(int var1);
}