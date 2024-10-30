package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.api.job.MultiProxyJob;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyJoinRequest;
import lombok.Data;

@Data
public class PartyRemoveJoinRequestJob implements MultiProxyJob
{

    private final Party party;
    private final PartyJoinRequest joinRequest;

    @Override
    public boolean isAsync()
    {
        return true;
    }

}
