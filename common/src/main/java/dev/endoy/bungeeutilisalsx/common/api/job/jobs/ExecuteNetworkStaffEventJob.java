package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.api.event.event.BUEvent;
import dev.endoy.bungeeutilisalsx.common.api.job.MultiProxyJob;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class ExecuteNetworkStaffEventJob implements MultiProxyJob
{

    private final String className;
    private final String userName;
    private final UUID uuid;
    private final String staffRank;

    public ExecuteNetworkStaffEventJob( final Class<? extends BUEvent> clazz, final String userName, final UUID uuid, final String staffRank )
    {
        this.className = clazz.getName();
        this.userName = userName;
        this.uuid = uuid;
        this.staffRank = staffRank;
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
