package be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item;

import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.spigot.utils.TriConsumer;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@RequiredArgsConstructor
public class ClickableGuiItem implements GuiItem
{

    private final ItemStack itemStack;
    private TriConsumer<Gui, Player, InventoryClickEvent> clickEventConsumer;

    @Override
    public GuiItem copy()
    {
        return new ClickableGuiItem( itemStack, clickEventConsumer );
    }

    @Override
    public ItemStack asItemStack()
    {
        return itemStack;
    }

    @Override
    public void onClick( final Gui gui, final Player player, final InventoryClickEvent event )
    {
        if ( clickEventConsumer != null )
        {
            clickEventConsumer.accept( gui, player, event );
        }
    }
}
