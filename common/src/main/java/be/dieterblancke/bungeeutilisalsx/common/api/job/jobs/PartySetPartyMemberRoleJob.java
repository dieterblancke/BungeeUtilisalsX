package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import lombok.Data;

import java.util.UUID;

@Data
public class PartySetPartyMemberRoleJob implements MultiProxyJob
{

    private final Party party;
    private final UUID uuid;
    private final String partyRole;

    @Override
    public boolean isAsync()
    {
        return true;
    }

}
