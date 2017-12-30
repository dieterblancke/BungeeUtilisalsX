package com.dbsoftwares.bungeeutilisals.api.experimental.inventory;

import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.Material;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

public interface Inventory {

    void setSize(int i);

    int getSize();

    String getTitle();

    ItemStack getItem(int slot);

    boolean setItem(int slot, ItemStack stack);

    boolean addItem(ItemStack stack);

    void destroy();

    TreeMap<Integer, ItemStack> addItem(ItemStack... items);

    TreeMap<Integer, ItemStack> removeItem(ItemStack... items);

    TreeMap<Integer, ItemStack> getContents();

    void setContents(TreeMap<Integer, ItemStack> items);

    boolean contains(Material material);

    boolean contains(ItemStack item);

    boolean containsAtLeast(Material material, int amount);

    boolean containsAtLeast(ItemStack item, int amount);

    TreeMap<Integer, ItemStack> all(Material material);

    TreeMap<Integer, ItemStack> all(ItemStack item);

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

    ListIterator<ItemStack> iterator(int index);

    InventoryUnsafe unsafe();
}