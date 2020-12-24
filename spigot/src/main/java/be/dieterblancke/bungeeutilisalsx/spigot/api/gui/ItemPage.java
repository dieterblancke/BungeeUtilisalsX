package be.dieterblancke.bungeeutilisalsx.spigot.api.gui;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.spigot.Bootstrap;
import be.dieterblancke.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiAction;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiActionType;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfigItem;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.handlers.CancelClickHandler;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.handlers.CloseClickHandler;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.handlers.NextPageClickHandler;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.handlers.PreviousPageClickHandler;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item.ClickableGuiItem;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item.GuiItem;
import be.dieterblancke.bungeeutilisalsx.spigot.utils.TriConsumer;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        final ItemStack itemStack = item.getItem().buildItem( placeholders );

        return this.getGuiItem( item.getAction(), item.getRightAction(), itemStack, placeholders );
    }

    protected GuiItem getGuiItem( final GuiAction action, final GuiAction rightAction, final ItemStack itemStack, final Object... placeholders )
    {
        final ClickableGuiItem clickableGuiItem = new ClickableGuiItem( itemStack )
                .addHandler( ClickType.LEFT, this.getClickHandler( action, placeholders ) );

        if ( rightAction != null && rightAction.isSet() )
        {
            clickableGuiItem.addHandler( ClickType.RIGHT, this.getClickHandler( rightAction, placeholders ) );
        }

        return clickableGuiItem;
    }

    private TriConsumer<Gui, Player, InventoryClickEvent> getClickHandler( final GuiAction guiAction, final Object... placeholders )
    {
        final String action = Utils.replacePlaceHolders( guiAction.getAction().trim(), placeholders );

        if ( guiAction.getType() == GuiActionType.INPUT )
        {
            return this.getInputClickHandler( guiAction, action );
        }
        else
        {
            return this.getCommandClickHandler( action );
        }
    }

    private TriConsumer<Gui, Player, InventoryClickEvent> getCommandClickHandler( final String action )
    {
        if ( action.contains( "close" ) )
        {
            return new CloseClickHandler();
        }
        else if ( action.equalsIgnoreCase( "previous-page" ) )
        {
            return new PreviousPageClickHandler();
        }
        else if ( action.equalsIgnoreCase( "next-page" ) )
        {
            return new NextPageClickHandler();
        }
        else if ( action.contains( "open:" ) )
        {
            final String[] args = action.replace( "open:", "" ).trim().split( " " );
            final String guiName = args[0];

            return ( gui, player, event ) ->
            {
                event.setCancelled( true );
                ( (BungeeUtilisalsX) BuX.getInstance() ).getGuiManager().openGui(
                        player, guiName, Arrays.copyOfRange( args, 1, args.length )
                );
            };
        }
        else if ( action.contains( "proxy-execute:" ) )
        {
            final String command = action.replace( "proxy-execute:", "" ).trim();

            return ( gui, player, event ) ->
            {
                event.setCancelled( true );
                player.closeInventory();
                this.sendProxyExecuteCommandPluginMessage( player, command );
            };
        }
        else
        {
            return new CancelClickHandler();
        }
    }

    private TriConsumer<Gui, Player, InventoryClickEvent> getInputClickHandler( final GuiAction guiAction, final String action )
    {
        return ( gui, player, event ) -> {
            event.setCancelled( true );

            Bukkit.getScheduler().runTaskLater(
                    Bootstrap.getInstance(),
                    () -> new AnvilGUI.Builder()
                            .title( guiAction.getConfigSection().getString( "title" ) )
                            .onComplete( ( p, output ) ->
                            {

                                final TriConsumer<Gui, Player, InventoryClickEvent> handler = this.getCommandClickHandler(
                                        action.replace( "{output}", output )
                                );

                                handler.accept( gui, p, event );
                                return AnvilGUI.Response.close();
                            } )
                            .plugin( Bootstrap.getInstance() )
                            .open( player ),
                    3
            );
        };
    }

    private void sendProxyExecuteCommandPluginMessage( final Player player, final String command )
    {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "commands" );
        out.writeUTF( "proxy-execute" );
        out.writeUTF( command );

        player.sendPluginMessage( Bootstrap.getInstance(), "bux:main", out.toByteArray() );
    }
}
