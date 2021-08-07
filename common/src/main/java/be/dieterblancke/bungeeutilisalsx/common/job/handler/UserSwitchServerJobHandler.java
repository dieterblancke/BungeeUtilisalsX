package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserSwitchServerJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.ServerCommandCall;

public class UserSwitchServerJobHandler extends AbstractJobHandler
{

    @JobHandler
    void handleUserSwitchServerJob( final UserSwitchServerJob job )
    {
        job.getTargetUser().ifPresent( user -> job.getTargetServer().ifPresent( proxyServer ->
                ServerCommandCall.sendToServer( user, proxyServer )
        ) );
    }
}
