package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.api.job.MultiProxyJob;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import lombok.Data;

@Data
public class PartyCreationJob implements MultiProxyJob
{

    private final Party party;

    @Override
    public boolean isAsync()
    {
        return true;
    }

}
