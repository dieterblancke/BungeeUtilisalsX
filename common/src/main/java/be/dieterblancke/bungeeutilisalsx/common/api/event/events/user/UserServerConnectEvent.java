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

    private final User user;
    private final ConnectReason connectReason;
    private IProxyServer target;
    private boolean cancelled;

    public UserServerConnectEvent( User user, IProxyServer target, ConnectReason connectReason )
    {
        this.user = user;
        this.connectReason = connectReason;
        this.target = target;
    }

    public enum ConnectReason
    {
        LOBBY_FALLBACK,
        COMMAND,
        SERVER_DOWN_REDIRECT,
        KICK_REDIRECT,
        PLUGIN_MESSAGE,
        JOIN_PROXY,
        PLUGIN,
        UNKNOWN;

        public static ConnectReason parse( String str )
        {
            for ( ConnectReason reason : values() )
            {
                if ( reason.toString().equalsIgnoreCase( str ) )
                {
                    return reason;
                }
            }
            return UNKNOWN;
        }
    }
}
