package com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.config;

import com.dbsoftwares.bungeeutilisalsx.spigot.utils.GuiSlotUtils;
import com.dbsoftwares.configuration.api.ISection;
import lombok.Data;

import java.util.Collection;

@Data
public class GuiConfigItem
{

    private final Collection<Integer> slots;
    private final String action;
    private final GuiConfigItemStack item;

    public GuiConfigItem( final GuiConfig guiConfig, final ISection section )
    {
        this.slots = GuiSlotUtils.formatSlots( guiConfig, section.getString( "slots" ).trim() );
        this.action = section.exists( "action" ) ? section.getString( "action" ) : "";
        this.item = section.exists( "item" ) ? new GuiConfigItemStack( section.getSection( "item" ) ) : null;
    }
}
