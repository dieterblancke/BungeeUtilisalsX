package dev.endoy.bungeeutilisalsx.common.api.event.events.user;

import dev.endoy.bungeeutilisalsx.common.api.event.AbstractEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Cancellable;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode( callSuper = true )
public class UserServerConnectedEvent extends AbstractEvent implements Cancellable
{

    private final User user;
    private final Optional<IProxyServer> previous;
    private final IProxyServer target;
    private boolean cancelled;

}
