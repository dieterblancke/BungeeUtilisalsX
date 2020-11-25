package com.dbsoftwares.bungeeutilisalsx.spigot.api.gui;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.utils.UuidInventoryHolder;
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
import java.util.UUID;

@Data
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public class Gui
{

    private final UUID uuid = UUID.randomUUID();
    private final String title;
    private final int rows;
    private final ItemProvider itemProvider;
    private final List<Player> players;
    private long lastActivity = System.currentTimeMillis();
    private Inventory inventory;
    private boolean opened;

    public static Builder builder()
    {
        return new Builder();
    }

    public void hancleInventoryClick( final InventoryClickEvent event )
    {
        lastActivity = System.currentTimeMillis();
        final GuiItem item = itemProvider.getItemAtSlot( event.getRawSlot() );

        if ( item != null )
        {
            item.onClick( this, (Player) event.getWhoClicked() );
        }
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
            final UuidInventoryHolder inventoryHolder = new UuidInventoryHolder( uuid );
            inventory = Bukkit.createInventory( inventoryHolder, rows * 9, Utils.c( title ) );
            inventoryHolder.setInventory( inventory );

            // TODO: fill inventory
        }
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
        private ItemProvider itemProvider;

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

        public Builder itemProvider( final ItemProvider itemProvider )
        {
            this.itemProvider = itemProvider;
            return this;
        }

        public Gui build()
        {
            final Gui gui = new Gui( title, rows, itemProvider, players );
            ( (BungeeUtilisalsX) BuX.getInstance() ).getGuiManager().add( gui );
            return gui;
        }
    }
}
