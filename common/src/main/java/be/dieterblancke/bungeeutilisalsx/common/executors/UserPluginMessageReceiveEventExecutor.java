package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserPluginMessageReceiveEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class UserPluginMessageReceiveEventExecutor implements EventExecutor
{

    @Event
    public void onUserPluginMessageReceive( final UserPluginMessageReceiveEvent event )
    {
        if ( !event.getChannel().equalsIgnoreCase( "bux:main" ) )
        {
            return;
        }
        final User user = event.getUser();
        final ByteArrayDataInput input = ByteStreams.newDataInput( event.getMessage() );
        final String subchannel = input.readUTF();

        if ( subchannel.equalsIgnoreCase( "commands" ) )
        {
            String action = input.readUTF();
            String command = input.readUTF();

            if ( action.equalsIgnoreCase( "proxy-execute" ) )
            {
                user.executeCommand( PlaceHolderAPI.formatMessage( user, command ) );
            }
            else if ( action.equalsIgnoreCase( "proxy-console-execute" ) )
            {
                BuX.getApi().getConsoleUser().executeCommand(
                        PlaceHolderAPI.formatMessage( BuX.getApi().getConsoleUser(), command )
                );
            }
        }
        else if ( subchannel.equalsIgnoreCase( "server-balancer" ) )
        {
            String serverName = input.readUTF();

            if ( BuX.getApi().getServerBalancer() != null )
            {
                ConfigFiles.SERVER_BALANCER_CONFIG.getServerBalancerGroupByName( serverName )
                        .or( () -> ConfigFiles.SERVER_BALANCER_CONFIG.getServerBalancerGroupFor( serverName ) )
                        .flatMap( balancerGroup -> BuX.getApi().getServerBalancer().getOptimalServer( balancerGroup ) )
                        .ifPresent( user::sendToServer );
            }
        }
    }
}
