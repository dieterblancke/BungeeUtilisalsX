package com.dbsoftwares.bungeeutilisalsx.bungee.commands;

import com.dbsoftwares.bungeeutilisalsx.bungee.hubbalancer.commands.HubCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.manager.CommandManager;

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
