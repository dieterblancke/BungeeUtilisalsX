package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserSwitchServerJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.commands.general.ServerCommandCall;

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
