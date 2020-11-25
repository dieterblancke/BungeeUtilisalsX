package com.dbsoftwares.bungeeutilisalsx.spigot.gui;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.TimeUnit;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.Gui;

import java.util.ArrayList;
import java.util.List;

public class GuiManager
{

    private final List<Gui> activeGuis = new ArrayList<>();

    public GuiManager()
    {
        // Automatically close inactive inventories.
        BuX.getInstance().getScheduler().runTaskRepeating( 1, 1, TimeUnit.MINUTES, () ->
                activeGuis.removeIf( gui ->
                {
                    if ( gui.getLastActivity() + TimeUnit.MINUTES.toMillis( 5 ) < System.currentTimeMillis() )
                    {
                        gui.close( false );
                        return true;
                    }
                    return false;
                } )
        );
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
}
