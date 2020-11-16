package com.dbsoftwares.bungeeutilisalsx.bungee.commands;

import com.dbsoftwares.bungeeutilisalsx.bungee.commands.general.ChatLockCommandCall;
import com.dbsoftwares.bungeeutilisalsx.bungee.commands.general.StaffChatCommandCall;
import com.dbsoftwares.bungeeutilisalsx.bungee.commands.general.domains.DomainsCommandCall;
import com.dbsoftwares.bungeeutilisalsx.bungee.hubbalancer.commands.HubCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.manager.CommandManager;

public class BungeeCommandManager extends CommandManager
{

    @Override
    protected void registerGeneralCommands()
    {
        super.registerGeneralCommands();
        registerGeneralCommand( "chatlock", new ChatLockCommandCall() );
        registerGeneralCommand( "staffchat", new StaffChatCommandCall() );
        registerGeneralCommand( "domains", new DomainsCommandCall() );

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
