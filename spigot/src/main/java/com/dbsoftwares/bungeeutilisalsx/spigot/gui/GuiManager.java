package com.dbsoftwares.bungeeutilisalsx.spigot.gui;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.TimeUnit;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.Gui;
import com.dbsoftwares.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
public class GuiManager
{

    private final List<Gui> activeGuis = new ArrayList<>();
    private FriendGuiConfig friendGuiConfig;

    public GuiManager()
    {
        // Automatically close inactive inventories and remove offline players from guis (this method mainly serves as a "fallback removal" of guis)
        BuX.getInstance().getScheduler().runTaskRepeating( 1, 1, TimeUnit.MINUTES, () ->
                activeGuis.removeIf( gui ->
                {
                    if ( gui.getLastActivity() + TimeUnit.MINUTES.toMillis( 5 ) < System.currentTimeMillis() )
                    {
                        gui.close( false );
                        return true;
                    }
                    else
                    {
                        gui.getPlayers().removeIf( p -> p == null || !p.isOnline() );
                    }
                    return false;
                } )
        );
        this.friendGuiConfig = new FriendGuiConfig();
    }

    public void reload()
    {
        this.closeAll();
        this.friendGuiConfig = new FriendGuiConfig();
    }

    public void closeAll()
    {
        for ( Gui gui : this.activeGuis )
        {
            gui.close( false );
        }
        this.activeGuis.clear();
    }

    public List<Gui> getActiveGuis()
    {
        return activeGuis;
    }

    public void add( final Gui gui )
    {
        this.activeGuis.add( gui );
    }

    public void remove( final Gui gui )
    {
        this.activeGuis.remove( gui );
    }

    public Optional<Gui> findByUuid( final UUID uuid )
    {
        return this.activeGuis.stream().filter( gui -> gui.getUuid().equals( uuid ) ).findAny();
    }
}
