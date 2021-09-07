package be.dieterblancke.bungeeutilisalsx.webapi.dto;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrackInfo;
import lombok.Value;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class TrackData
{

    UUID uuid;
    String trackId;
    String server;
    String executedBy;
    LocalDateTime date;
    boolean active;

    public static TrackData of( final PunishmentTrackInfo info )
    {
        return new TrackData(
                info.getUuid(),
                info.getTrackId(),
                info.getServer(),
                info.getExecutedBy(),
                new Timestamp( info.getDate().getTime() ).toLocalDateTime(),
                info.isActive()
        );
    }
}
