package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.api.job.jobs.ChatLockJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.commands.general.ChatLockCommandCall;

public class ChatLockJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeChatLockJob( final ChatLockJob job )
    {
        ChatLockCommandCall.lockChat( job.getServerName(), job.getBy() );
    }
}
