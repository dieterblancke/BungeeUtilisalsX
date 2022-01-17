package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import lombok.Data;

import java.util.UUID;

@Data
public class PartySetChatModeJob implements MultiProxyJob
{

    private final Party party;
    private final UUID uuid;
    private final boolean chat;

    @Override
    public boolean isAsync()
    {
        return true;
    }

}
