package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyJoinRequest;
import lombok.Data;

@Data
public class PartyAddJoinRequestJob implements MultiProxyJob
{

    private final Party party;
    private final PartyJoinRequest joinRequest;

    @Override
    public boolean isAsync()
    {
        return true;
    }

}
