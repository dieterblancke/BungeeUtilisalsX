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

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@StorageTable(name = "{friendrequests-table}",
        indexes = {"userid", "friendid"}, primary = {"userid", "friendid"},
        foreign = {"userid => {users-table}(id)", "friendid => {users-table}(id)"})
public class FriendRequestTable {

    @StorageColumn(type = "INT(11)", nullable = false)
    private int userid;

    @StorageColumn(type = "INT(11)", nullable = false)
    private int friendid;

    @StorageColumn(type = "DATETIME", nullable = false, def = "CURRENT_TIMESTAMP")
    private Date friendsince;

}