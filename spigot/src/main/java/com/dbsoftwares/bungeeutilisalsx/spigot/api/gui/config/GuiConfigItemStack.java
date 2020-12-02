package com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.config;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.bungeeutilisalsx.spigot.utils.ItemUtils;
import com.dbsoftwares.configuration.api.ISection;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class GuiConfigItemStack
{

    private final int data;
    private final String name;
    private final List<String> lore;
    private final String owner;
    private final List<GuiConfigEnchantment> enchantments;
    private Material material;

    public GuiConfigItemStack( final ISection section )
    {
        try
        {
            this.material = Material.matchMaterial( section.getString( "material" ) );
        }
        catch ( Exception e )
        {
            this.material = Material.AIR;
            BuX.getLogger().warning( "Could not find a Material for " + section.getString( "material" ) );
        }
        this.data = section.exists( "data" ) ? section.getInteger( "data" ) : 0;
        this.name = section.exists( "name" ) ? section.getString( "name" ) : "";
        this.lore = section.exists( "lores" ) ? section.getStringList( "lores" ) : new ArrayList<>();
        this.owner = section.exists( "owner" ) ? section.getString( "owner" ) : null;
        this.enchantments = section.exists( "enchants" ) ? section.getSectionList( "enchants" )
                .stream()
                .map( GuiConfigEnchantment::new )
                .collect( Collectors.toList() )
                : new ArrayList<>();
    }

    public ItemStack buildItem( final Object... placeholders )
    {
        if ( material == null || material == Material.AIR )
        {
            return new ItemStack( Material.AIR );
        }
        final ItemStack itemStack = new ItemStack( material );

        if ( data != 0 )
        {
            itemStack.setDurability( (short) data );
        }
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName( Utils.c( Utils.replacePlaceHolders( name, placeholders ) ) );
        itemMeta.setLore(
                this.lore.stream()
                        .map( lore -> Utils.c( Utils.replacePlaceHolders( lore, placeholders ) ) )
                        .collect( Collectors.toList() )
        );

        for ( GuiConfigEnchantment enchantment : this.enchantments )
        {
            itemMeta.addEnchant(
                    enchantment.getEnchantment(),
                    enchantment.getLevel(),
                    enchantment.getLevel() > enchantment.getEnchantment().getMaxLevel()
            );
        }
        if ( owner != null && !owner.trim().isEmpty() )
        {
            ItemUtils.setSkullTexture( itemMeta, owner.trim() );
        }
        itemStack.setItemMeta( itemMeta );
        return itemStack;
    }
}
