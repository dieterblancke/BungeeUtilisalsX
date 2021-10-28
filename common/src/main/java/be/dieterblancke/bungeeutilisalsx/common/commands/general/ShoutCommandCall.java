package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;

public class ShoutCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final String message = String.join( " ", args );
        String shoutMessagePath = "general-commands.shout.shout-broadcast";
        if ( user.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "shout.staff-permission" ) ) )
        {
            shoutMessagePath += "staff";
        }

        BuX.getApi().langBroadcast(
                shoutMessagePath,
                "{user}", user.getName(),
                "{servername}", user.getServerName(),
                "{message}", message
        );
    }

    @Override
    public String getDescription()
    {
        return "Sends a global shout. This is a simplified version of /announce that can be used as a donator perk. Staff can have a custom shout format.";
    }

    @Override
    public String getUsage()
    {
        return "/shout (message)";
    }
}
