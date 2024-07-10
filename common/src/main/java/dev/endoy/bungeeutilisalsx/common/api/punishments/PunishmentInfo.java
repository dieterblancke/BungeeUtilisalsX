package dev.endoy.bungeeutilisalsx.common.api.punishments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PunishmentInfo
{

    private PunishmentType type;

    private String id;
    private String user;
    private String ip;
    private UUID uuid;
    private String executedBy;
    private String server;
    private String reason;
    private Date date;

    // Not applicable for all punishments
    private Long expireTime;
    private boolean active;
    private String removedBy;
    private String punishmentUid;

    public boolean isActivatable()
    {
        return type == null || type.isActivatable();
    }

    public boolean isTemporary()
    {
        return type != null && type.isTemporary() && expireTime != null;
    }

    public boolean isExpired()
    {
        return isTemporary() && expireTime <= System.currentTimeMillis();
    }

    public boolean isLoaded()
    {
        return user != null && ip != null && uuid != null && executedBy != null && reason != null;
    }
}