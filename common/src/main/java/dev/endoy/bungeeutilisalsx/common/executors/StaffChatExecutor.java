package dev.endoy.bungeeutilisalsx.common.executors;

import dev.endoy.bungeeutilisalsx.common.api.event.event.Event;
import dev.endoy.bungeeutilisalsx.common.api.event.event.EventExecutor;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Priority;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.commands.general.StaffChatCommandCall;
import dev.endoy.configuration.api.IConfiguration;

public class StaffChatExecutor implements EventExecutor
{

    @Event( priority = Priority.LOWEST )
    public void onStaffChat( final UserChatEvent event )
    {
        if ( event.isCancelled() )
        {
            return;
        }
        final User user = event.getUser();

        if ( user.isInStaffChat() )
        {
            if ( user.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "staffchat.permission" ) ) )
            {
                event.setCancelled( true );

                StaffChatCommandCall.sendStaffChatMessage( user, event.getMessage() );
            }
            else
            {
                user.setInStaffChat( false );
            }
        }
    }

    @Event( priority = Priority.LOWEST )
    public void onCharChat( final UserChatEvent event )
    {
        final IConfiguration config = ConfigFiles.GENERALCOMMANDS.getConfig();
        final String detect = config.getString( "staffchat.charchat.detect" );

        if ( !config.getBoolean( "staffchat.enabled" )
                || !config.getBoolean( "staffchat.charchat.enabled" )
                || !event.getMessage().startsWith( detect ) )
        {
            return;
        }
        final User user = event.getUser();
        final String permission = config.getString( "staffchat.permission" );
        if ( !user.hasPermission( permission ) || user.isInStaffChat() )
        {
            return;
        }
        final String message = event.getMessage().substring( detect.length() );

        StaffChatCommandCall.sendStaffChatMessage( user, message );
        event.setCancelled( true );
    }
}
