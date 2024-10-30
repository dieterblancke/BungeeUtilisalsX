package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.api.job.jobs.ChatSyncJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

public class ChatSyncJobHandler extends AbstractJobHandler
{

    @JobHandler
    void handleChatSyncJob( final ChatSyncJob job )
    {
        ConfigFiles.SERVERGROUPS.getServer( job.getServerGroupName() ).ifPresent( serverGroup ->
        {
            for ( IProxyServer server : serverGroup.getServers() )
            {
                if ( job.getServerToSkip() != null && job.getServerToSkip().equals( server.getName() ) )
                {
                    continue;
                }

                for ( User user : server.getUsers() )
                {
                    user.sendRawColorMessage( job.getMessage() );
                }
            }
        } );
    }
}
