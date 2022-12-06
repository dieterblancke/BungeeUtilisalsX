package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyWarpMembersJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import lombok.SneakyThrows;

public class PartyWarpMembersJobHandler extends AbstractJobHandler
{

    @JobHandler
    @SneakyThrows
    void handlePartyWarpMembersJob( final PartyWarpMembersJob job )
    {
        final IProxyServer server = BuX.getInstance().serverOperations().getServerInfo( job.getTargetServer() );

        if ( server != null )
        {
            job.getOnlineMembersToWarp().forEach( user ->
            {
                user.sendToServer( server );
                user.sendLangMessage( "party.warp.warped", MessagePlaceholders.create().append( "server", job.getTargetServer() ) );
            } );
        }
    }
}
