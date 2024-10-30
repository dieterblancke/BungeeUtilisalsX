package dev.endoy.bungeeutilisalsx.common.protocolize.gui.item;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.TriConsumer;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.Gui;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.item.ItemStack;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ClickableGuiItem implements GuiItem
{

    private final ItemStack itemStack;
    private final Map<ClickType, TriConsumer<Gui, User, InventoryClick>> handlerMap;

    public ClickableGuiItem( final ItemStack itemStack )
    {
        this( itemStack, new HashMap<>() );
    }

    public ClickableGuiItem addHandler( final ClickType clickType,
                                        final TriConsumer<Gui, User, InventoryClick> handler )
    {
        this.handlerMap.put( clickType, handler );
        return this;
    }

    @Override
    public GuiItem copy()
    {
        return new ClickableGuiItem( itemStack, handlerMap );
    }

    @Override
    public ItemStack asItemStack()
    {
        return itemStack;
    }

    @Override
    public void onClick( final Gui gui, final User user, final InventoryClick event )
    {
        if ( handlerMap.containsKey( event.clickType() ) )
        {
            handlerMap.get( event.clickType() ).accept( gui, user, event );
        }
        else if ( !handlerMap.isEmpty() )
        {
            handlerMap.values().stream().findFirst().ifPresent( consumer -> consumer.accept( gui, user, event ) );
        }
    }
}
