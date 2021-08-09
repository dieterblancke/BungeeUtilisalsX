package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.CommandSpyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

public class CommandSpyJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeCommandSpyJob( final CommandSpyJob job )
    {
        final String permission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "commandspy.permission" );

        BuX.getApi().getUsers()
                .stream()
                .filter( user -> user.isCommandSpy() && user.hasPermission( permission ) )
                .filter( user -> !user.getUuid().equals( job.getUuid() ) )
                .forEach( user ->
                {
                    user.sendLangMessage(
                            "general-commands.commandspy.message",
                            "{user}", job.getUserName(),
                            "{server}", job.getServerName(),
                            "{command}", job.getCommand()
                    );
                } );
    }
}
