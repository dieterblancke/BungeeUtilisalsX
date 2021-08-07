package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.ClearChatJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.ClearChatCommandCall;

public class ClearChatJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeClearChatJob( final ClearChatJob job )
    {
        ClearChatCommandCall.clearChat( job.getServerName(), job.getBy() );
    }
}
