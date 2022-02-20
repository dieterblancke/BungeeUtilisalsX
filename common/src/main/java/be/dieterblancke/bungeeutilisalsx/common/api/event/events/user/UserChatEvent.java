package be.dieterblancke.bungeeutilisalsx.common.api.event.events.user;

import be.dieterblancke.bungeeutilisalsx.common.api.event.AbstractEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Cancellable;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This event will be executed upon User Chat. UserChatPreExecuteEvent is being executed AFTER this event.
 */
@Data
@EqualsAndHashCode( callSuper = true )
public class UserChatEvent extends AbstractEvent implements Cancellable
{

    private User user;
    private String message;
    private boolean cancelled = false;

    public UserChatEvent( final User user, final String message )
    {
        this.user = user;
        this.message = message;
    }
}
