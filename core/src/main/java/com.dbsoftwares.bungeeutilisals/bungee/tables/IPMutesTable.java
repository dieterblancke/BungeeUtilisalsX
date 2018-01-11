package com.dbsoftwares.bungeeutilisals.bungee.tables;

/*
 * Created by DBSoftwares on 05/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageColumn;
import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageTable;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@StorageTable(name = "{ipmutes-table}", indexes = {"uuid", "user", "active", "ip"}, foreign = {"uuid => {users-table}(uuid)"})
public class IPMutesTable {

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
    private Date date;

    @StorageColumn(type = "TINYINT(1)", nullable = false)
    private boolean active;

    @StorageColumn(type = "VARCHAR(32)", nullable = false)
    private String executedby;

    @StorageColumn(type = "VARCHAR(32)")
    private String removedby;

    public static IPMutesTable fromInfo(PunishmentInfo info) {
        IPMutesTable table = new IPMutesTable();

        table.setUuid(info.getUuid());
        table.setUser(info.getUser());
        table.setIp(info.getIP());
        table.setReason(info.getReason());
        table.setServer(info.getServer());
        table.setActive(true);
        table.setExecutedby(info.getBy());

        return table;
    }
}