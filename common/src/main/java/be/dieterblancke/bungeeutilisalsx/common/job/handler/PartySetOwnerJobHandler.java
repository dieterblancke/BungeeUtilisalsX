package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyAddMemberJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartySetOwnerJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import lombok.SneakyThrows;

public class PartySetOwnerJobHandler extends AbstractJobHandler
{

    @JobHandler
    @SneakyThrows
    void handlePartySetOwnerJob( final PartySetOwnerJob job )
    {
        BuX.getInstance().getPartyManager().getCurrentPartyByUuid( job.getParty().getUuid() ).ifPresent( party -> {
            for ( PartyMember partyMember : party.getPartyMembers() )
            {
                partyMember.setPartyOwner( partyMember.getUuid().equals( job.getNewOwner() ) );
            }
        } );
    }
}
