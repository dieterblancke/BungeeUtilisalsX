package dev.endoy.bungeeutilisalsx.common.protocolize.guis.custom;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;

public class CustomItemPage extends ItemPage
{

    public CustomItemPage( final User user,
                           final GuiConfig guiConfig )
    {
        super( guiConfig.getRows() * 9 );

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            for ( int slot : item.getSlots() )
            {
                super.setItem(
                        slot,
                        this.getGuiItem(
                                item.getAction(),
                                item.getRightAction(),
                                item.getItem().buildItem( user )
                        )
                );
            }
        }
    }
}
