package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.api.job.MultiProxyJob;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyMember;
import lombok.Data;

@Data
public class PartyAddMemberJob implements MultiProxyJob
{

    private final Party party;
    private final PartyMember partyMember;

    @Override
    public boolean isAsync()
    {
        return true;
    }

}
