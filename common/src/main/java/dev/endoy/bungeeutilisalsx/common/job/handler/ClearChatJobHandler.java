package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.api.job.jobs.ClearChatJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.commands.general.ClearChatCommandCall;

public class ClearChatJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeClearChatJob( final ClearChatJob job )
    {
        ClearChatCommandCall.clearChat( job.getServerName(), job.getBy() );
    }
}
