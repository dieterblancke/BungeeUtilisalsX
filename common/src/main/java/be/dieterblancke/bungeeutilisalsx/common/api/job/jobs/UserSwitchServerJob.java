package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
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
        return Optional.ofNullable( BuX.getInstance().proxyOperations().getServerInfo( server ) );
    }
}
