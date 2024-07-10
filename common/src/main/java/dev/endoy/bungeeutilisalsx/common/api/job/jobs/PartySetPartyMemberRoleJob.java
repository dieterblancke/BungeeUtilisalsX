package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.api.job.MultiProxyJob;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
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
