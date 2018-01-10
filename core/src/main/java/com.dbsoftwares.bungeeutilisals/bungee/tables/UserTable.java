package com.dbsoftwares.bungeeutilisals.bungee.tables;

/*
 * Created by DBSoftwares on 05/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageColumn;
import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageTable;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@StorageTable(name = "{users-table}", indexes = {"uuid", "username", "ip"})
public class UserTable {

    @StorageColumn(type = "INT(11)", primary = true, nullable = false, autoincrement = true, updateable = false)
    private int id;

    @StorageColumn(type = "VARCHAR(64)", nullable = false, unique = true, updateable = false)
    private String uuid;

    @StorageColumn(type = "VARCHAR(24)", nullable = false, unique = true)
    private String username;

    @StorageColumn(type = "VARCHAR(32)", nullable = false)
    private String ip;

    @StorageColumn(type = "VARCHAR(32)", nullable = false)
    private String language;

    public static UserTable getDefault(ProxiedPlayer p) {
        UserTable table = new UserTable();
        table.setUuid(p.getUniqueId().toString());
        table.setUsername(p.getName());
        table.setIp(Utils.getIP(p.getAddress()));

        Optional<Language> optionalLanguage = BungeeUtilisals.getApi().getLanguageManager().getDefaultLanguage();
        if (optionalLanguage.isPresent()) {
            table.setLanguage(optionalLanguage.get().getName());
        } else {
            table.setLanguage("en_US");
        }

        return table;
    }
}