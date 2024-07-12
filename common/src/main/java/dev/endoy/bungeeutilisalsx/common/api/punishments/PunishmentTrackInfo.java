package dev.endoy.bungeeutilisalsx.common.api.punishments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PunishmentTrackInfo
{

    private UUID uuid;
    private String trackId;
    private String server;
    private String executedBy;
    private Date date;
    private boolean active;

}