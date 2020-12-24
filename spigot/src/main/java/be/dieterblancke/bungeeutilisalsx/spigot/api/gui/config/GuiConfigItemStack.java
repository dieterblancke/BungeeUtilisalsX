package be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.spigot.utils.ItemUtils;
import be.dieterblancke.bungeeutilisalsx.spigot.utils.LanguageUtils;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        this.lore = section.exists( "lores" )
                ? section.isString( "lores" ) ? Lists.newArrayList( section.getString( "lores" ) ) : section.getStringList( "lores" )
                : new ArrayList<>();
        this.owner = section.exists( "owner" ) ? section.getString( "owner" ) : null;
        this.enchantments = section.exists( "enchants" ) ? section.getSectionList( "enchants" )
                .stream()
                .map( GuiConfigEnchantment::new )
                .collect( Collectors.toList() )
                : new ArrayList<>();
    }

    public ItemStack buildItem( final Player player, final Object... placeholders )
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
        itemMeta.setDisplayName( Utils.c( Utils.replacePlaceHolders(
                LanguageUtils.getLanguageString( name, player ),
                placeholders
        ) ) );
        final List<String> lores = lore.size() == 1 ? LanguageUtils.getLanguageStringList( lore.get( 0 ), player ) : lore;
        itemMeta.setLore(
                lores.stream()
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
