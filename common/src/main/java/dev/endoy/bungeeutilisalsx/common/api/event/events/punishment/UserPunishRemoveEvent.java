package dev.endoy.bungeeutilisalsx.common.api.event.events.punishment;

import dev.endoy.bungeeutilisalsx.common.api.event.AbstractEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Cancellable;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@EqualsAndHashCode( callSuper = true )
@Data
public class UserPunishRemoveEvent extends AbstractEvent implements Cancellable
{

    private PunishmentRemovalAction action;
    private User executor;
    private UUID uuid;
    private String name;
    private String ip;
    private String executionServer;
    private Date date = new Date( System.currentTimeMillis() );
    private boolean cancelled = false;

    public UserPunishRemoveEvent( PunishmentRemovalAction action, User executor, UUID uuid, String name, String ip, String executionServer )
    {
        this.action = action;
        this.executor = executor;
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.executionServer = executionServer;
    }

    public Optional<User> getUser()
    {
        return getApi().getUser( name );
    }

    public enum PunishmentRemovalAction
    {
        UNBAN, UNBANIP, UNMUTE, UNMUTEIP
    }
}