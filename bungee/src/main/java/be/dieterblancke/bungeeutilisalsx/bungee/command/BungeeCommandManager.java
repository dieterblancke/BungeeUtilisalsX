package be.dieterblancke.bungeeutilisalsx.bungee.command;

import be.dieterblancke.bungeeutilisalsx.bungee.hubbalancer.commands.HubCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.manager.CommandManager;

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
