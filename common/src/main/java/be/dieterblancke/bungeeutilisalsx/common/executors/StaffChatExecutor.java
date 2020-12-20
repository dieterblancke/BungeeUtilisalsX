package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.BridgeType;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.bridge.types.UserAction;
import be.dieterblancke.bungeeutilisalsx.common.bridge.types.UserActionType;
import be.dieterblancke.bungeeutilisalsx.common.bridge.util.BridgedUserMessage;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.StaffChatCommandCall;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StaffChatExecutor implements EventExecutor
{

    @Event
    public void onStaffChat( final UserChatEvent event )
    {
        if ( event.isCancelled() )
        {
            return;
        }
        final User user = event.getUser();

        if ( user.isInStaffChat() )
        {
            if ( user.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "staffchat.permission" ) )
                    || user.hasPermission( "bungeeutilisals.commands.*" )
                    || user.hasPermission( "bungeeutilisals.*" )
                    || user.hasPermission( "*" ) )
            {
                event.setCancelled( true );

                StaffChatCommandCall.sendStaffChatMessage( user.getServerName(), user.getName(), event.getMessage() );

                if ( BuX.getApi().getBridgeManager().useBridging() )
                {
                    BuX.getApi().getBridgeManager().getBridge().sendTargetedMessage(
                            BridgeType.BUNGEE_BUNGEE,
                            null,
                            Lists.newArrayList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
                            "USER",
                            new UserAction(
                                    null,
                                    UserActionType.MESSAGE,
                                    new BridgedUserMessage(
                                            true,
                                            "general-commands.staffchat.format",
                                            Maps.newHashMap(),
                                            "{user}", user.getName(),
                                            "{server}", user.getServerName(),
                                            "{message}", event.getMessage()
                                    )
                            )
                    );
                }
            }
            else
            {
                user.setInStaffChat( false );
            }
        }
    }

    @Event
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

        StaffChatCommandCall.sendStaffChatMessage( user.getServerName(), user.getName(), message );
        event.setCancelled( true );
    }
}
