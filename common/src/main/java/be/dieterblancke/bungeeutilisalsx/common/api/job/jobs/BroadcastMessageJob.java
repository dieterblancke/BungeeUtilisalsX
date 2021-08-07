package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class BroadcastMessageJob implements MultiProxyJob
{

    private final String prefix;
    private final String message;
    private final String permission;

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
