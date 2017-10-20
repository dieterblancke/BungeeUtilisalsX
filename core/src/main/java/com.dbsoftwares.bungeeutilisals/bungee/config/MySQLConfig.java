package com.dbsoftwares.bungeeutilisals.bungee.config;

/*
 * Created by DBSoftwares on 16 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.configuration.api.Config;
import com.dbsoftwares.bungeeutilisals.api.configuration.api.ConfigPath;

import java.io.File;

public class MySQLConfig extends Config {

    public MySQLConfig(File datafolder) {
        super.init(this, new File(datafolder, "mysql.yml"));
    }

    @ConfigPath("hostname")
    public String hostname = "127.0.0.1";

    @ConfigPath("port")
    public Integer port = 3306;

    @ConfigPath("database")
    public String database = "database";

    @ConfigPath("username")
    public String username = "username";

    @ConfigPath("password")
    public String password = "password";
}