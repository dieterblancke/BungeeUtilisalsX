package be.dieterblancke.bungeeutilisalsx.common.api.event.events.user;

import be.dieterblancke.bungeeutilisalsx.common.api.event.AbstractEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Cancellable;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode( callSuper = true )
public class UserServerConnectedEvent extends AbstractEvent implements Cancellable
{

    private final User user;
    private final IProxyServer previous;
    private final IProxyServer target;
    private boolean cancelled;

}
