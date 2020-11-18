package com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user;

import com.dbsoftwares.bungeeutilisalsx.common.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.Cancellable;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.other.IProxyServer;
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
