package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.api.job.MultiProxyJob;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
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
