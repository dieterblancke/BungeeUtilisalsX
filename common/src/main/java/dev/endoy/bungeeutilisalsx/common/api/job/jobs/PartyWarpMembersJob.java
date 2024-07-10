package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.MultiProxyJob;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
public class PartyWarpMembersJob implements MultiProxyJob
{

    private final UUID partyUuid;
    private final List<UUID> membersToWarp;
    private final String targetServer;

    @Override
    public boolean isAsync()
    {
        return true;
    }

    public List<User> getOnlineMembersToWarp()
    {
        return membersToWarp
                .stream()
                .map( BuX.getApi()::getUser )
                .filter( Optional::isPresent )
                .map( Optional::get )
                .toList();
    }
}
