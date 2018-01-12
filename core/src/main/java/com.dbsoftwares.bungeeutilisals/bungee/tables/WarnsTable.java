package com.dbsoftwares.bungeeutilisals.bungee.tables;

/*
 * Created by DBSoftwares on 05/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageColumn;
import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageTable;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentTable;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@StorageTable(name = "{warns-table}", indexes = {"uuid", "user", "ip"}, foreign = {"uuid => {users-table}(uuid)"})
public class WarnsTable implements PunishmentTable {

    @StorageColumn(type = "INT(11)", primary = true, nullable = false, autoincrement = true)
    private int id;

    @StorageColumn(type = "VARCHAR(64)", nullable = false)
    private String uuid;

    @StorageColumn(type = "VARCHAR(24)", nullable = false)
    private String user;

    @StorageColumn(type = "VARCHAR(32)", nullable = false)
    private String ip;

    @StorageColumn(type = "TEXT", nullable = false)
    private String reason;

    @StorageColumn(type = "VARCHAR(32)", nullable = false)
    private String server;

    @StorageColumn(type = "DATETIME", nullable = false, def = "CURRENT_TIMESTAMP")
    private String date;

    @StorageColumn(type = "VARCHAR(32)", nullable = false)
    private String executedby;

    public static WarnsTable fromInfo(PunishmentInfo info) {
        WarnsTable table = new WarnsTable();

        table.setUuid(info.getUuid());
        table.setUser(info.getUser());
        table.setIp(info.getIP());
        table.setReason(info.getReason());
        table.setServer(info.getServer());
        table.setExecutedby(info.getBy());

        return table;
    }

    @Override
    public PunishmentType getType() {
        return PunishmentType.WARN;
    }

    @Override
    public Boolean isActive() {
        return null;
    }

    @Override
    public String getRemovedby() {
        return null;
    }

    @Override
    public Long getRemoveTime() {
        return null;
    }
}