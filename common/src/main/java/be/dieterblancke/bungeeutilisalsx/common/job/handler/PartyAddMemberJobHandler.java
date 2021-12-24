package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyAddMemberJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyCreationJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import lombok.SneakyThrows;

public class PartyAddMemberJobHandler extends AbstractJobHandler
{

    @JobHandler
    @SneakyThrows
    void handlePartyAddMemberJob( final PartyAddMemberJob job )
    {
        BuX.getInstance().getPartyManager().getCurrentPartyByUuid( job.getParty().getUuid() ).ifPresent( party -> {
            party.getPartyMembers().add( job.getPartyMember() );
        } );
    }
}
