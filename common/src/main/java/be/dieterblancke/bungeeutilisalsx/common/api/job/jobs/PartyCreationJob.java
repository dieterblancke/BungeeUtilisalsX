package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
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
