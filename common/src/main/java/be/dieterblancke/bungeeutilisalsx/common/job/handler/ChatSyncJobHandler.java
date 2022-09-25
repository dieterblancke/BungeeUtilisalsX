package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.ChatSyncJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;

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
