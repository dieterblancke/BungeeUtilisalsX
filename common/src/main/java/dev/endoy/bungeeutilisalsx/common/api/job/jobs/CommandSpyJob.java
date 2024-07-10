package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.api.job.HasUserJob;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CommandSpyJob extends HasUserJob
{

    private final String serverName;
    private final String command;

    public CommandSpyJob( final UUID uuid, final String userName, final String serverName, final String command )
    {
        super( uuid, userName );

        this.serverName = serverName;
        this.command = command;
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
