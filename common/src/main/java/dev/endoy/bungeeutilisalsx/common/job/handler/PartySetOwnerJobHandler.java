package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.PartySetOwnerJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyMember;
import lombok.SneakyThrows;

public class PartySetOwnerJobHandler extends AbstractJobHandler
{

    @JobHandler
    @SneakyThrows
    void handlePartySetOwnerJob( final PartySetOwnerJob job )
    {
        BuX.getInstance().getPartyManager().getCurrentPartyByUuid( job.getParty().getUuid() ).ifPresent( party ->
        {
            for ( PartyMember partyMember : party.getPartyMembers() )
            {
                if ( partyMember.getUuid().equals( job.getUuid() ) )
                {
                    partyMember.setPartyOwner( job.isOwner() );
                }
            }
        } );
    }
}
