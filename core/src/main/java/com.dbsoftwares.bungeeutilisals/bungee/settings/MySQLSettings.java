package com.dbsoftwares.bungeeutilisals.bungee.settings;

import com.dbsoftwares.bungeeutilisals.api.settings.SettingStorage;
import com.dbsoftwares.bungeeutilisals.api.settings.SettingType;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import lombok.Getter;

@Getter
public class MySQLSettings extends SettingStorage {

    private String host, database, username, password;
    private Integer port;

    public MySQLSettings() {
        super(BungeeUtilisals.getInstance(), SettingType.MYSQL);
    }

    @Override
    public void reload() {
        super.reloadConfig();

        load();
    }

    @Override
    public void load() {
        this.host = getConfig().getString("host");
        this.port = getConfig().getInt("port");
        this.database = getConfig().getString("database");
        this.username = getConfig().getString("username");
        this.password = getConfig().getString("password");
    }
}