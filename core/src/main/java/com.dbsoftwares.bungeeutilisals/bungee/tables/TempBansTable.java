package com.dbsoftwares.bungeeutilisals.bungee.tables;

/*
 * Created by DBSoftwares on 05/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageColumn;
import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@StorageTable(name = "{tempbans-table}", indexes = {"uuid", "user", "active", "ip"}, foreign = {"uuid => {users-table}(uuid)"})
public class TempBansTable {

    @StorageColumn(type = "INT(11)", primary = true, nullable = false, autoincrement = true)
    private int id;

    @StorageColumn(type = "VARCHAR(64)", nullable = false)
    private String uuid;

    @StorageColumn(type = "VARCHAR(24)", nullable = false)
    private String user;

    @StorageColumn(type = "VARCHAR(32)", nullable = false)
    private String ip;

    @StorageColumn(type = "LONG", nullable = false)
    private Long removeTime;

    @StorageColumn(type = "TEXT", nullable = false)
    private String reason;

    @StorageColumn(type = "VARCHAR(32)", nullable = false)
    private String server;

    @StorageColumn(type = "DATETIME", nullable = false, def = "CURRENT_TIMESTAMP")
    private String date;

    @StorageColumn(type = "BOOLEAN", nullable = false)
    private boolean active;

    @StorageColumn(type = "VARCHAR(32)", nullable = false)
    private String executedby;

    @StorageColumn(type = "VARCHAR(32)")
    private String removedby;

    public TempBansTable(String uuid, String user, String ip, long removeTime, String reason, String server, boolean active, String executor) {
        this.uuid = uuid;
        this.user = user;
        this.ip = ip;
        this.removeTime = removeTime;
        this.reason = reason;
        this.server = server;
        this.active = active;
        this.executedby = executor;
    }
}