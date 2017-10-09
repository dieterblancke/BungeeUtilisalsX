package com.dbsoftwares.bungeeutilisals.bungee.configuration;

/*
 * Created by DBSoftwares on 04 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.universal.configuration.Config;
import com.dbsoftwares.bungeeutilisals.universal.configuration.ConfigPath;

import java.io.File;

public class MainConfig extends Config {

    public MainConfig() {
        super.init(this, new File(BungeeUtilisals.getInstance().getDataFolder(), "config.yml"));
    }

    @ConfigPath("prefix")
    public String prefix = "&e&lBungeeUtilisals &8»";

}