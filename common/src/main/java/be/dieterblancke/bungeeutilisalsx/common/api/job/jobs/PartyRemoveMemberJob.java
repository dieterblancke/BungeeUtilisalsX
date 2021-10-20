package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import lombok.Data;

@Data
public class PartyRemoveMemberJob implements MultiProxyJob
{

    private final Party party;
    private final PartyMember partyMember;

    @Override
    public boolean isAsync()
    {
        return true;
    }

}
