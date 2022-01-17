package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartySetChatModeJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import lombok.SneakyThrows;

public class PartySetChatModeHandler extends AbstractJobHandler
{

    @JobHandler
    @SneakyThrows
    void handlePartySetChatModeJob( final PartySetChatModeJob job )
    {
        BuX.getInstance().getPartyManager().getCurrentPartyByUuid( job.getParty().getUuid() ).ifPresent( party ->
        {
            for ( PartyMember partyMember : party.getPartyMembers() )
            {
                if ( partyMember.getUuid().equals( job.getUuid() ) )
                {
                    partyMember.setChat( job.isChat() );
                }
            }
        } );
    }
}
