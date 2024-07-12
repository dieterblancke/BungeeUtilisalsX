package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.PartyWarpMembersJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
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
