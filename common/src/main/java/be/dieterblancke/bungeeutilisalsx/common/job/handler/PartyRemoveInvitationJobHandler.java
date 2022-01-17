package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyRemoveInvitationJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import lombok.SneakyThrows;

public class PartyRemoveInvitationJobHandler extends AbstractJobHandler
{

    @JobHandler
    @SneakyThrows
    void handlePartyRemoveInvitationJob( final PartyRemoveInvitationJob job )
    {
        BuX.getInstance().getPartyManager().getCurrentPartyByUuid( job.getParty().getUuid() ).ifPresent( party ->
        {
            party.getSentInvites().removeIf( invite -> job.getPartyInvite().getInvitee().equals( invite.getInvitee() ) );
        } );
    }
}
