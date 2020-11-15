package com.dbsoftwares.bungeeutilisals.commands.general;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.utils.config.ConfigFiles;

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

        BUCore.getApi().langBroadcast(
                shoutMessagePath,
                "{user}", user.getName(),
                "{servername}", user.getServerName(),
                "{message}", message
        );
    }
}
