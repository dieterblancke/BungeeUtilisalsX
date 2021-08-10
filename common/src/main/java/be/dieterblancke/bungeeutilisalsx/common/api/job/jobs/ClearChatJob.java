package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ClearChatJob implements MultiProxyJob
{

    private final String serverName;
    private final String by;

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
