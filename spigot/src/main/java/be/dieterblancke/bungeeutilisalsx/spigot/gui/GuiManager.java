package be.dieterblancke.bungeeutilisalsx.spigot.gui;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friendactions.FriendActionsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.opener.FriendActionsGuiOpener;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.opener.FriendGuiOpener;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.*;

@Data
public class GuiManager
{

    private final List<Gui> activeGuis = new ArrayList<>();
    private final List<GuiOpener> guiOpeners = new ArrayList<>();
    private FriendGuiConfig friendGuiConfig;
    private FriendActionsGuiConfig friendActionsGuiConfig;

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
        this.friendActionsGuiConfig = new FriendActionsGuiConfig();
        this.registerGuiOpeners(
                new FriendGuiOpener(),
                new FriendActionsGuiOpener()
        );
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

    public void openGui( final Player player, final String guiName, final String[] args )
    {
        this.findGuiOpener( guiName ).ifPresent( opener -> opener.openGui( player, args ) );
    }
}
