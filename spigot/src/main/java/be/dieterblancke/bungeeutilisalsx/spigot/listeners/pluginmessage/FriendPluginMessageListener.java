package be.dieterblancke.bungeeutilisalsx.spigot.listeners.pluginmessage;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.GuiOpener;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Optional;

public class FriendPluginMessageListener implements PluginMessageListener
{
    @Override
    public void onPluginMessageReceived( final String channel, final Player player, final byte[] message )
    {
        if ( !channel.equalsIgnoreCase( "bux:main" ) )
        {
            return;
        }
        final ByteArrayDataInput input = ByteStreams.newDataInput( message );
        final String subchannel = input.readUTF();

        if ( subchannel.equalsIgnoreCase( "friends:gui" ) )
        {
            final String action = input.readUTF();

            if ( action.equalsIgnoreCase( "open" ) )
            {
                this.openGuiFor( input.readUTF(), input.readUTF() );
            }
        }
    }

    private void openGuiFor( final String gui, final String user )
    {
        final Player player = Bukkit.getPlayer( user );

        if ( player != null )
        {
            final Optional<GuiOpener> optionalGuiOpener = ( (BungeeUtilisalsX) BuX.getInstance() ).getGuiManager().findGuiOpener( gui );

            if ( optionalGuiOpener.isPresent() )
            {
                final GuiOpener guiOpener = optionalGuiOpener.get();

                guiOpener.openGui( player, new String[0] );
            }
        }
    }
}
