package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.ChatLockJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.ChatLockCommandCall;

public class ChatLockJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeChatLockJob( final ChatLockJob job )
    {
        ChatLockCommandCall.lockChat( job.getServerName(), job.getBy() );
    }
}
