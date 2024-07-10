package dev.endoy.bungeeutilisalsx.common.protocolize.guis.parties;

import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItemStack;
import dev.endoy.configuration.api.ISection;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode( callSuper = true )
public class PartyGuiConfigItem extends GuiConfigItem
{

    private final boolean memberItem;
    private final GuiConfigItemStack offlineItem;
    private final GuiConfigItemStack onlineItem;

    public PartyGuiConfigItem( final GuiConfig guiConfig, final ISection section )
    {
        super( guiConfig, section );

        this.memberItem = section.exists( "party-slots" ) && section.getBoolean( "party-slots" );

        if ( this.memberItem )
        {
            this.offlineItem = section.exists( "offlineitem" ) ? new GuiConfigItemStack( section.getSection( "offlineitem" ) ) : null;
            this.onlineItem = section.exists( "onlineitem" ) ? new GuiConfigItemStack( section.getSection( "onlineitem" ) ) : null;
        }
        else
        {
            this.offlineItem = null;
            this.onlineItem = null;
        }
    }
}
