package dev.endoy.bungeeutilisalsx.common.protocolize.guis.custom;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.PageableItemProvider;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;

import java.util.Optional;

public class CustomGuiItemProvider implements PageableItemProvider
{

    private final ItemPage page;

    public CustomGuiItemProvider( final User user, final GuiConfig config )
    {
        this.page = new CustomItemPage( user, config );
    }

    @Override
    public Optional<GuiItem> getItemAtSlot( final int page, final int rawSlot )
    {
        return this.getItemContents( page ).getItem( rawSlot );
    }

    @Override
    public ItemPage getItemContents( final int page )
    {
        return this.page;
    }

    @Override
    public int getPageAmount()
    {
        return 1;
    }
}
