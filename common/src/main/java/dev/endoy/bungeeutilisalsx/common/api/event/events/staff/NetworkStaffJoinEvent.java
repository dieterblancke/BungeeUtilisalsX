package dev.endoy.bungeeutilisalsx.common.api.event.events.staff;

import dev.endoy.bungeeutilisalsx.common.api.event.AbstractEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Cancellable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * This event will be executed upon network join
 */
@Getter
@Setter
@EqualsAndHashCode( callSuper = true )
public class NetworkStaffJoinEvent extends AbstractEvent implements Cancellable
{

    private final String userName;
    private final UUID uuid;
    private final String staffRank;

    private boolean cancelled;

    public NetworkStaffJoinEvent( String userName, UUID uuid, String staffRank )
    {
        this.userName = userName;
        this.uuid = uuid;
        this.staffRank = staffRank;
    }
}
