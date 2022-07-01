package be.dieterblancke.bungeeutilisalsx.common.api.event.events.user;

import be.dieterblancke.bungeeutilisalsx.common.api.event.AbstractEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Cancellable;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode( callSuper = true )
public class UserServerKickEvent extends AbstractEvent implements Cancellable
{

    private User user;
    private IProxyServer kickedFrom;
    private IProxyServer redirectServer;
    private boolean cancelled;

}