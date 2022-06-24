package be.dieterblancke.bungeeutilisalsx.common.api.event.events.user;

import be.dieterblancke.bungeeutilisalsx.common.api.event.AbstractEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Cancellable;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;

/**
 * This event is being executed upon User Command execute.
 */
@Data
@EqualsAndHashCode( callSuper = true )
public class UserCommandEvent extends AbstractEvent implements Cancellable
{

    private User user;
    private String command;
    private boolean cancelled = false;

    public UserCommandEvent( final User user, final String command )
    {
        this.user = user;
        this.command = command;
    }

    public String getActualCommand()
    {
        return command.split( " " )[0].toLowerCase();
    }

    public String[] getArguments()
    {
        final String[] arguments = command.split( " " );

        return Arrays.copyOfRange( arguments, 1, arguments.length );
    }
}
