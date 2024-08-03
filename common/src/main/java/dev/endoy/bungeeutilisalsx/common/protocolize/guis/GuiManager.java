package dev.endoy.bungeeutilisalsx.common.protocolize.guis;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.TimeUnit;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.Gui;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import lombok.Data;

import java.util.*;

@Data
public class GuiManager
{

    private final List<Gui> activeGuis = new ArrayList<>();
    private final List<GuiOpener> guiOpeners = new ArrayList<>();

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
                return false;
            } )
        );

        for ( DefaultGui gui : DefaultGui.values() )
        {
            gui.loadConfig();
            this.registerGuiOpeners( gui.getGuiOpenerSupplier().get() );
        }
    }

    public Optional<GuiOpener> findGuiOpener( final String name )
    {
        return this.guiOpeners.stream().filter( opener -> opener.getName().equalsIgnoreCase( name ) ).findFirst();
    }

    public void registerGuiOpeners( final GuiOpener... guiOpeners )
    {
        this.guiOpeners.addAll( Arrays.asList( guiOpeners ) );
    }

    public void reload()
    {
        this.closeAll();

        for ( DefaultGui gui : DefaultGui.values() )
        {
            gui.loadConfig();
        }

        for ( GuiOpener guiOpener : this.guiOpeners )
        {
            guiOpener.reload();
        }
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

    public void openGui( final User user, final String guiName, final String[] args )
    {
        this.findGuiOpener( guiName ).ifPresent( opener -> opener.openGui( user, args ) );
    }
}
