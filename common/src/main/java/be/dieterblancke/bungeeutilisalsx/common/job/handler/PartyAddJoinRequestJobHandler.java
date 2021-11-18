package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyAddJoinRequestJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import lombok.SneakyThrows;

public class PartyAddJoinRequestJobHandler extends AbstractJobHandler
{

    @JobHandler
    @SneakyThrows
    void handlePartyAddJoinRequestJob( final PartyAddJoinRequestJob job )
    {
        BuX.getInstance().getPartyManager().getCurrentPartyByUuid( job.getParty().getUuid() ).ifPresent( party ->
        {
            party.getJoinRequests().add( job.getJoinRequest() );
        } );
    }
}
