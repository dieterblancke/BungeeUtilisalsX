package be.dieterblancke.bungeeutilisalsx.spigot.api.gui;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item.GuiItem;
import be.dieterblancke.bungeeutilisalsx.spigot.api.utils.UuidInventoryHolder;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public class Gui
{

    private final UUID uuid = UUID.randomUUID();
    private final String title;
    private final int rows;
    private final PageableItemProvider pageableItemProvider;
    private final List<Player> players;
    private long lastActivity = System.currentTimeMillis();
    private Inventory inventory;
    private boolean opened;
    private int page = 1;

    public static Builder builder()
    {
        return new Builder();
    }

    public void handleInventoryClick( final InventoryClickEvent event )
    {
        lastActivity = System.currentTimeMillis();
        final Optional<GuiItem> item = pageableItemProvider.getItemAtSlot( event.getRawSlot() );

        item.ifPresent( i -> i.onClick( this, (Player) event.getWhoClicked(), event ) );
    }

    public void open()
    {
        lastActivity = System.currentTimeMillis();
        this.buildInventory();

        for ( Player player : players )
        {
            player.openInventory( inventory );
        }
        opened = true;
    }

    public void close( final boolean remove )
    {
        if ( remove )
        {
            ( (BungeeUtilisalsX) BuX.getInstance() ).getGuiManager().remove( this );
        }

        for ( Player player : players )
        {
            player.closeInventory();
        }
        opened = false;

        if ( remove )
        {
            players.clear();
        }
    }

    public void close()
    {
        this.close( true );
    }

    public void buildInventory()
    {
        if ( inventory == null )
        {
            this.refill();
        }
    }

    public void setPage( final int page )
    {
        if ( page < 1 )
        {
            this.page = 1;
        }
        else
        {
            this.page = Math.min( page, pageableItemProvider.getPageAmount() );
        }
    }

    public void refill()
    {
        final UuidInventoryHolder inventoryHolder = new UuidInventoryHolder( uuid );
        inventory = Bukkit.createInventory( inventoryHolder, rows * 9, Utils.c(
                Utils.replacePlaceHolders( title, "{page}", page, "{max}", pageableItemProvider.getPageAmount() )
        ) );
        inventoryHolder.setInventory( inventory );

        final ItemPage itemPage = pageableItemProvider.getItemContents( page );
        itemPage.populateTo( inventory );
    }

    public void addPlayer( final Player player )
    {
        if ( opened )
        {
            player.openInventory( inventory );
        }
    }

    public void removePlayer( final Player player )
    {
        if ( opened )
        {
            player.closeInventory();
        }
    }

    public static final class Builder
    {
        private String title;
        private int rows = 6;
        private List<Player> players = new ArrayList<>();
        private PageableItemProvider pageableItemProvider;

        private Builder()
        {
            // keep constructor private
        }

        public Builder title( final String title )
        {
            this.title = title;
            return this;
        }

        public Builder rows( final int rows )
        {
            this.rows = rows;
            return this;
        }

        public Builder players( final Iterable<Player> players )
        {
            this.players = Lists.newArrayList( players );
            return this;
        }

        public Builder players( final Player... players )
        {
            this.players = Lists.newArrayList( players );
            return this;
        }

        public Builder itemProvider( final PageableItemProvider pageableItemProvider )
        {
            this.pageableItemProvider = pageableItemProvider;
            return this;
        }

        public Gui build()
        {
            final Gui gui = new Gui( title, rows, pageableItemProvider, players );
            ( (BungeeUtilisalsX) BuX.getInstance() ).getGuiManager().add( gui );
            return gui;
        }
    }
}
