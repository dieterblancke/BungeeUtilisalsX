package com.dbsoftwares.bungeeutilisals.api.punishmentts;

/*
 * Created by DBSoftwares on 24 februari 2017
 * Developer: Dieter Blancke
 * Project: CMS
 * May only be used for CentrixPVP
 */

public enum PunishmentType {

    BAN("Ban"), IPBAN("IPBan"), MUTE("Mute");

    String name;

    PunishmentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}