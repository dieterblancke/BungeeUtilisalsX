package be.dieterblancke.bungeeutilisalsx.common.api.event.events.staff;

import be.dieterblancke.bungeeutilisalsx.common.api.event.AbstractEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Cancellable;
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

    public NetworkStaffJoinEvent( final String userName, final UUID uuid, final String staffRank )
    {
        this.userName = userName;
        this.uuid = uuid;
        this.staffRank = staffRank;
    }
}
