package com.dbsoftwares.bungeeutilisals.api.experimental.item;

import com.dbsoftwares.bungeeutilisals.api.experimental.packets.nbt.NBTBase;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.nbt.NBTTagCompound;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.nbt.NBTTagList;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.nbt.NBTTagString;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.skin.GameProfile;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemMeta {

    static final ItemMetaKey NAME = new ItemMetaKey("Name", "display-name");
    static final ItemMetaKey DISPLAY = new ItemMetaKey("display");
    static final ItemMetaKey LORE = new ItemMetaKey("Lore", "lore");
    static final ItemMetaKey ENCHANTMENTS = new ItemMetaKey("ench", "enchants");
    static final ItemMetaKey ENCHANTMENTS_ID = new ItemMetaKey("id");
    static final ItemMetaKey ENCHANTMENTS_LVL = new ItemMetaKey("lvl");
    static final ItemMetaKey HIDEFLAGS = new ItemMetaKey("HideFlags", "ItemFlags");
    static final ItemMetaKey UNBREAKABLE = new ItemMetaKey("Unbreakable");
    static final ItemMetaKey SKULL_OWNER = new ItemMetaKey("SkullOwner", "skull-owner");

    private NBTTagCompound tag = new NBTTagCompound();

    public String getDisplayName() {
        return tag.getString(NAME.NBT);
    }

    public ItemMeta setDisplayName(String displayname) {
        setDisplayTag(NAME.NBT, new NBTTagString(displayname));
        return this;
    }

    public String getOwner() {
        if (!tag.hasKey("SkullOwner")) {
            return null;
        }
        NBTTagCompound skullowner = tag.getCompound("SkullOwner");
        return skullowner.hasKey("Id") ? skullowner.getString("Id") : skullowner.getString("Name");
    }

    public ItemMeta setOwner(UUID id) {
        try {
            GameProfile profile = new GameProfile(id, null);
            if (profile.loadSkin()) {
                NBTTagCompound compound = profile.serialize(new NBTTagCompound());
                this.tag.set("SkullOwner", compound);
            } else {
                throw new Exception();
            }
        } catch (Exception ignored) { }
        return this;
    }

    public ItemMeta setOwner(String name) {
        try {
            GameProfile profile = new GameProfile(null, name);
            if (profile.loadSkin()) {
                NBTTagCompound compound = profile.serialize(new NBTTagCompound());
                this.tag.set("SkullOwner", compound);
            } else {
                throw new Exception("load skin fail");
            }
        } catch (Exception ignored) { }
        return this;
    }

    public List<String> getLore() {
        return fromNBTList(tag.getList(LORE.NBT));
    }

    public ItemMeta setLore(List<String> lore) {
        setDisplayTag(LORE.NBT, createStringList(lore));
        return this;
    }

    public ItemMeta setLore(String... lore) {
        setDisplayTag(LORE.NBT, createStringList(Arrays.asList(lore)));
        return this;
    }

    public ItemMeta removeLore() {
        return setLore(Lists.newArrayList());
    }

    public ItemMeta addFakeGlow() {
        return addEnchantment(Enchantment.FAKE_GLOW, 1);
    }

    public Map<Enchantment, Integer> getEnchantments() {
        NBTTagList list = tag.getList(ENCHANTMENTS.NBT);

        if (list == null) {
            return Maps.newHashMap();
        }
        Map<Enchantment, Integer> enchantments = Maps.newHashMap();
        for (NBTBase base : list.getList()) {
            if (base instanceof NBTTagCompound) {
                NBTTagCompound nbtTagCompound = (NBTTagCompound) base;

                short enchantId = nbtTagCompound.getShort(ENCHANTMENTS_ID.NBT);
                short enchantLvl = nbtTagCompound.getShort(ENCHANTMENTS_LVL.NBT);

                Enchantment enchantment = Enchantment.getById(enchantId);
                enchantments.put(enchantment, (int) enchantLvl);
            }
        }
        return enchantments;
    }

    public ItemMeta addEnchantment(Enchantment enchant, int level) {
        NBTTagList list = tag.getList(ENCHANTMENTS.NBT);

        if (list == null) {
            list = new NBTTagList();
        }

        NBTTagCompound subtag = new NBTTagCompound();
        subtag.setShort(ENCHANTMENTS_ID.NBT, (short) enchant.getId());
        subtag.setShort(ENCHANTMENTS_LVL.NBT, (short) level);

        list.add(subtag);

        tag.set(ENCHANTMENTS.NBT, list);

        return this;
    }

    private List<String> fromNBTList(NBTTagList nbt) {
        List<String> list = Lists.newArrayList();

        for (NBTBase base : nbt.getList()) {
            if (base instanceof NBTTagString) {
                list.add(((NBTTagString) base).a_());
            }
        }

        return list;
    }

    private NBTTagList createStringList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        NBTTagList nbt = new NBTTagList();
        list.forEach(s -> nbt.add(new NBTTagString(s)));
        return nbt;
    }

    private void setDisplayTag(String key, NBTBase value) {
        NBTTagCompound display = tag.getCompound(DISPLAY.NBT);

        if (display == null) {
            display = new NBTTagCompound();
        }

        if (!tag.hasKey(DISPLAY.NBT)) {
            tag.set(DISPLAY.NBT, display);
        }

        display.set(key, value);
    }

    protected void fromTag(NBTTagCompound tag) {
        this.tag = tag;
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    static class ItemMetaKey {
        final String BUKKIT;
        final String NBT;

        ItemMetaKey(String both) {
            this(both, both);
        }

        ItemMetaKey(String nbt, String bukkit) {
            this.NBT = nbt;
            this.BUKKIT = bukkit;
        }

        @interface Specific {
            To value();

            enum To {
                BUKKIT,
                NBT
            }
        }
    }

    public Boolean isSimilar(ItemMeta meta) {
        return tag.equals(meta.getTag());
    }
}