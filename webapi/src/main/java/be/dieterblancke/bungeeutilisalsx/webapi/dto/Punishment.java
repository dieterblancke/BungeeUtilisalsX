package be.dieterblancke.bungeeutilisalsx.webapi.dto;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import lombok.Value;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class Punishment
{

    PunishmentType type;
    String user;
    UUID uuid;
    String ip;
    String executedById;
    String server;
    String reason;
    LocalDateTime dateTime;
    long expireTime;
    boolean active;
    String removedBy;
    String punishmentUid;

    public static Punishment of( final PunishmentInfo info )
    {
        return new Punishment(
                info.getType(),
                info.getUser(),
                info.getUuid(),
                ConfigFiles.CONFIG.getConfig().getBoolean( "expose-ips" ) ? info.getIp() : "",
                info.getExecutedBy(),
                info.getServer(),
                info.getReason(),
                new Timestamp( info.getDate().getTime() ).toLocalDateTime(),
                info.getExpireTime(),
                info.isActive(),
                info.getRemovedBy(),
                info.getPunishmentUid()
        );
    }
}
