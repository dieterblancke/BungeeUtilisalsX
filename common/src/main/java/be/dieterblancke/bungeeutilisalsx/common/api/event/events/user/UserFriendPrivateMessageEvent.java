package be.dieterblancke.bungeeutilisalsx.common.api.event.events.user;

import be.dieterblancke.bungeeutilisalsx.common.api.event.AbstractEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * This event is being executed upon User Friend Private Message.
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode( callSuper = true )
public class UserFriendPrivateMessageEvent extends AbstractEvent
{

    private final String sender;
    private final String receiver;
    private final String message;

}
