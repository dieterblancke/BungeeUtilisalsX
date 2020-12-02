package com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.config;

import com.dbsoftwares.configuration.api.ISection;
import lombok.Data;
import org.bukkit.enchantments.Enchantment;

@Data
public class GuiConfigEnchantment
{

    private final Enchantment enchantment;
    private final int level;

    public GuiConfigEnchantment( final ISection section )
    {
        this.enchantment = section.exists( "enchantment" ) ? Enchantment.getByName( section.getString( "enchantment" ) ) : null;
        this.level = section.exists( "level" ) ? section.getInteger( "level" ) : 1;
    }
}
