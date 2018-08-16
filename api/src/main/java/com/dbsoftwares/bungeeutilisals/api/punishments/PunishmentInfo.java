package com.dbsoftwares.bungeeutilisals.api.punishments;

/*
 * Created by DBSoftwares on 20/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PunishmentInfo {

    private PunishmentType type;

    private int id;
    private String user;
    private String IP;
    private UUID uuid;
    private String executedBy;
    private String server;
    private String reason;
    private Date date;

    // Not applicable for all punishments
    private Long expireTime;
    private boolean active;
    private String removedBy;

    public boolean isActivatable() {
        return type.isActivatable();
    }

    public boolean isTemporary() {
        return type.isTemporary();
    }
}