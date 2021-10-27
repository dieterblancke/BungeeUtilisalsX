package be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item;

import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TriConsumer;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ClickableGuiItem implements GuiItem
{

    private final ItemStack itemStack;
    private final Map<ClickType, TriConsumer<Gui, Player, InventoryClickEvent>> handlerMap;

    public ClickableGuiItem( final ItemStack itemStack )
    {
        this( itemStack, new HashMap<>() );
    }

    public ClickableGuiItem addHandler( final ClickType clickType,
                                        final TriConsumer<Gui, Player, InventoryClickEvent> handler )
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
    public void onClick( final Gui gui, final Player player, final InventoryClickEvent event )
    {
        if ( handlerMap.containsKey( event.getClick() ) )
        {
            handlerMap.get( event.getClick() ).accept( gui, player, event );
        }
        else if ( !handlerMap.isEmpty() )
        {
            handlerMap.values().stream().findFirst().ifPresent( consumer -> consumer.accept( gui, player, event ) );
        }
    }
}
