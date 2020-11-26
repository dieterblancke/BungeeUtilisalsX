package com.dbsoftwares.bungeeutilisalsx.velocity.command;

import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.manager.CommandManager;
import com.dbsoftwares.bungeeutilisalsx.velocity.hubbalancer.commands.HubCommandCall;

public class BungeeCommandManager extends CommandManager
{

    @Override
    protected void registerGeneralCommands()
    {
        super.registerGeneralCommands();

        if ( ConfigFiles.HUBBALANCER.isEnabled() )
        {
            registerCommand(
                    "hub",
                    ConfigFiles.HUBBALANCER.getConfig().getSection( "commands.hub" ),
                    new HubCommandCall()
            );
        }
    }
}
