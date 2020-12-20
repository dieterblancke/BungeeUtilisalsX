package be.dieterblancke.bungeeutilisalsx.spigot.api.gui;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfigItem;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item.*;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Optional;

public class ItemPage
{

    private final GuiItem[] items;

    public ItemPage( final int slots )
    {
        items = new GuiItem[slots];
    }

    public void setRange( final GuiItem item, final int start, final int end )
    {
        this.setRange( item, start, end, false );
    }

    public void setRange( final GuiItem item, final int start, final int end, final boolean clone )
    {
        for ( int i = start; i < end; i++ )
        {
            this.setItem( i, clone ? item.copy() : item );
        }
    }

    public void setItem( final int slot, final GuiItem item )
    {
        items[slot] = item;
    }

    public void removeItem( final int slot )
    {
        this.setItem( slot, null );
    }

    public Optional<GuiItem> getItem( final int slot )
    {
        if ( slot < 0 || slot >= items.length )
        {
            return Optional.empty();
        }
        return Optional.ofNullable( items[slot] );
    }

    public void populateTo( final Inventory inventory )
    {
        for ( int i = 0; i < items.length; i++ )
        {
            inventory.setItem( i, items[i] == null ? null : items[i].asItemStack() );
        }
    }

    protected boolean shouldShow( final int page, final int max, final GuiConfigItem item )
    {
        if ( item.getShowIf().equalsIgnoreCase( "has-previous-page" ) )
        {
            return page > 0;
        }
        else if ( item.getShowIf().equalsIgnoreCase( "has-next-page" ) )
        {
            return page < max - 1;
        }
        else
        {
            return true;
        }
    }

    protected GuiItem getGuiItem( final GuiConfigItem item, final Object... placeholders )
    {
        final String action = Utils.replacePlaceHolders( item.getAction().toLowerCase().trim(), placeholders );
        final ItemStack itemStack = item.getItem().buildItem( placeholders );

        return this.getGuiItem( action, itemStack );
    }

    protected GuiItem getGuiItem( final String action, final ItemStack itemStack )
    {
        if ( action.contains( "close" ) )
        {
            return new ClosingGuiItem( itemStack );
        }
        else if ( action.equalsIgnoreCase( "previous-page" ) )
        {
            return new PreviousPageGuiItem( itemStack );
        }
        else if ( action.equalsIgnoreCase( "next-page" ) )
        {
            return new NextPageGuiItem( itemStack );
        }
        else if ( action.contains( "open:" ) )
        {
            final String[] args = action.replace( "open:", "" ).trim().split( " " );
            final String guiName = args[0];

            return new ClickableGuiItem(
                    itemStack,
                    ( gui, player, event ) ->
                    {
                        event.setCancelled( true );
                        ( (BungeeUtilisalsX) BuX.getInstance() ).getGuiManager().openGui(
                                player, guiName, Arrays.copyOfRange( args, 1, args.length )
                        );
                    }
            );
        }
        else if ( action.contains( "execute:" ) )
        {
            final String command = action.replace( "execute:", "" ).trim();

            return new ClickableGuiItem(
                    itemStack,
                    ( gui, player, event ) ->
                    {
                        event.setCancelled( true );
                        // TODO: send redis pubsub message to bungeecord to execute this command as the player
                        player.closeInventory();
                    }
            );
        }
        else
        {
            return new CancelledGuiItem( itemStack );
        }
    }
}
