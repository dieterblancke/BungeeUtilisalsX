package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.PartySetPartyMemberRoleJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyMember;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
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
