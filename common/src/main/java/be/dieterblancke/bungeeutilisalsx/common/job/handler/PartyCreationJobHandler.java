package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyCreationJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import lombok.SneakyThrows;

public class PartyCreationJobHandler extends AbstractJobHandler
{

    @JobHandler
    @SneakyThrows
    void handlePartyCreationJob( final PartyCreationJob job )
    {
        BuX.getInstance().getPartyManager().registerPartyLocally( job.getParty() );
    }
}
