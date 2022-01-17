package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyInvite;
import lombok.Data;

@Data
public class PartyAddInvitationJob implements MultiProxyJob
{

    private final Party party;
    private final PartyInvite partyInvite;

    @Override
    public boolean isAsync()
    {
        return true;
    }

}
