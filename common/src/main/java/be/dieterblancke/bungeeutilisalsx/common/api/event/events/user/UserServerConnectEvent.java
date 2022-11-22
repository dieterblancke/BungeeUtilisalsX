package be.dieterblancke.bungeeutilisalsx.common.api.event.events.user;

import be.dieterblancke.bungeeutilisalsx.common.api.event.AbstractEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Cancellable;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper = true )
public class UserServerConnectEvent extends AbstractEvent implements Cancellable
{

    private User user;
    private IProxyServer target;
    private boolean cancelled;

    public UserServerConnectEvent( final User user, final IProxyServer target )
    {
        this.user = user;
        this.target = target;
    }
}
