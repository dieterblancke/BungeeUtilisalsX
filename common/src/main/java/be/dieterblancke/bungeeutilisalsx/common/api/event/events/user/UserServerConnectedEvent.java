package be.dieterblancke.bungeeutilisalsx.common.api.event.events.user;

import be.dieterblancke.bungeeutilisalsx.common.api.event.AbstractEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Cancellable;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper = true )
public class UserServerConnectedEvent extends AbstractEvent implements Cancellable
{

    private User user;
    private IProxyServer target;
    private boolean cancelled;

    public UserServerConnectedEvent( final User user, final IProxyServer target )
    {
        this.user = user;
        this.target = target;
    }
}
