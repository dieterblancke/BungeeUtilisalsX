package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.api.job.MultiProxyJob;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyInvite;
import lombok.Data;

@Data
public class PartyRemoveInvitationJob implements MultiProxyJob
{

    private final Party party;
    private final PartyInvite partyInvite;

    @Override
    public boolean isAsync()
    {
        return true;
    }

}
