package dev.endoy.bungeeutilisalsx.common.commands.general;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;

public class StaffAnnouncementCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "general-commands.staffannouncement.usage" );
            return;
        }
        final String message = String.join( " ", args );

        BuX.getApi().langPermissionBroadcast(
            "general-commands.staffannouncement.broadcast",
            ConfigFiles.GENERALCOMMANDS.getConfig().getString( "staffannouncement.receive-permission" ),
            MessagePlaceholders.create()
                .append( "broadcaster", user.getName() )
                .append( "message", message )
        );
    }

    @Override
    public String getDescription()
    {
        return "Sends an announcement only to people that have a specific permission.";
    }

    @Override
    public String getUsage()
    {
        return "/staffannouncement (message)";
    }
}
