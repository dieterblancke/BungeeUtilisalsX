package dev.endoy.bungeeutilisalsx.common.api.event.events.user;

import dev.endoy.bungeeutilisalsx.common.api.event.AbstractEvent;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * This event gets called if an User was successfully saved and logged out.
 */
@AllArgsConstructor
@EqualsAndHashCode( callSuper = true )
public class UserUnloadEvent extends AbstractEvent
{

    @Getter
    private final User user;

}