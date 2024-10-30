package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.MultiProxyJob;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class UserSwitchServerJob implements MultiProxyJob
{

    private final String targetName;
    private final String server;

    public UserSwitchServerJob( final String targetName,
                                final String server )
    {
        this.targetName = targetName;
        this.server = server;
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }

    public Optional<User> getTargetUser()
    {
        return BuX.getApi().getUser( targetName );
    }

    public Optional<IProxyServer> getTargetServer()
    {
        return Optional.ofNullable( BuX.getInstance().serverOperations().getServerInfo( server ) );
    }
}
