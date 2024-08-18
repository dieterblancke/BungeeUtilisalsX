package dev.endoy.bungeeutilisalsx.common.api.event.events.punishment;

import dev.endoy.bungeeutilisalsx.common.api.event.AbstractEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Cancellable;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@EqualsAndHashCode( callSuper = true )
@Data
public class UserPunishEvent extends AbstractEvent implements Cancellable
{

    private PunishmentType type;
    private User executor;
    private UUID uuid;
    private String name;
    private String ip;
    private String reason;
    private String executionServer;
    private Long expire;
    private Date date = new Date( System.currentTimeMillis() );

    @Getter
    @Setter
    private boolean cancelled = false;

    public UserPunishEvent( PunishmentType type, User executor, UUID uuid, String name, String ip, String reason, String executionServer, Long expire )
    {
        this.type = type;
        this.executor = executor;
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.reason = reason;
        this.executionServer = executionServer;
        this.expire = expire;
    }

    public PunishmentInfo getInfo()
    {
        return new PunishmentInfo(
            type,
            "0",
            name,
            ip,
            uuid,
            executor.getName(),
            executionServer,
            reason,
            date,
            expire,
            true,
            null,
            null
        );
    }

    public boolean isActivatable()
    {
        return type.isActivatable();
    }

    public boolean isTemporary()
    {
        return type.isTemporary();
    }

    public Optional<User> getUser()
    {
        return getApi().getUser( name );
    }

    public boolean isMute()
    {
        return type.toString().contains( "MUTE" );
    }

    public boolean isBan()
    {
        return type.toString().contains( "BAN" );
    }

    public boolean isKick()
    {
        return type.equals( PunishmentType.KICK );
    }

    public boolean isWarn()
    {
        return type.equals( PunishmentType.WARN );
    }

    public boolean isIPPunishment()
    {
        return type.toString().startsWith( "IP" );
    }

    public boolean isUserPunishment()
    {
        return !type.toString().startsWith( "IP" );
    }
}