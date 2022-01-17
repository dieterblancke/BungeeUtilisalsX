package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartySetPartyMemberRoleJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import lombok.SneakyThrows;

public class PartySetPartyMemberRoleHandler extends AbstractJobHandler
{

    @JobHandler
    @SneakyThrows
    void handlePartySetPartyMemberRoleJob( final PartySetPartyMemberRoleJob job )
    {
        BuX.getInstance().getPartyManager().getCurrentPartyByUuid( job.getParty().getUuid() ).ifPresent( party ->
        {
            for ( PartyMember partyMember : party.getPartyMembers() )
            {
                if ( partyMember.getUuid().equals( job.getUuid() ) )
                {
                    partyMember.setPartyRole( ConfigFiles.PARTY_CONFIG.findPartyRole( job.getPartyRole() )
                            .orElse( ConfigFiles.PARTY_CONFIG.getDefaultRole() ) );
                }
            }
        } );
    }
}
