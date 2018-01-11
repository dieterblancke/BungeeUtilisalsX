package com.dbsoftwares.bungeeutilisals.api.punishments;

/*
 * Created by DBSoftwares on 24 februari 2017
 * Developer: Dieter Blancke
 * Project: CMS
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import lombok.Data;

@Data
public class PunishmentInfo {

    private String uuid;
    private String user;
    private String IP;
    private String by;
    private Long time;
    private String bandatetime = Utils.getCurrentDate();
    private String reason;
    private String server;
    private PunishmentType type;

    public Boolean isExpired() {
        return time != null && time != -1 && time < System.currentTimeMillis();
    }
}