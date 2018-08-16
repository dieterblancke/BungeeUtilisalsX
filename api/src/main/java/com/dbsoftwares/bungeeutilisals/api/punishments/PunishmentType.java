package com.dbsoftwares.bungeeutilisals.api.punishments;

/*
 * Created by DBSoftwares on 20/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public enum PunishmentType {

    BAN(true, false), TEMPBAN(true, true), IPBAN(true, false), IPTEMPBAN(true, true),
    MUTE(true, false), TEMPMUTE(true, true), IPMUTE(true, false), IPTEMPMUTE(true, true),
    KICK(false, false), WARN(false, false);

    private final boolean activatable;
    private final boolean temporary;

    PunishmentType(boolean activatable, boolean temporary) {
        this.activatable = activatable;
        this.temporary = temporary;
    }

    public boolean isActivatable() {
        return activatable;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public String getTablePlaceHolder() {
        return "{" + toString().toLowerCase() + "s-table}";
    }

    public boolean isIP() {
        return toString().startsWith("IP");
    }
}