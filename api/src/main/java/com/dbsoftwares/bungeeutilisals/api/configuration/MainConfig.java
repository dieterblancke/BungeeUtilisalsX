package com.dbsoftwares.bungeeutilisals.api.configuration;

/*
 * Created by DBSoftwares on 04 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.configuration.api.Config;
import com.dbsoftwares.bungeeutilisals.api.configuration.api.ConfigPath;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainConfig extends Config {

    public MainConfig() {
        super.init(this, new File(BUCore.getApi().getPlugin().getDataFolder(), "config.yml"));
    }

    @ConfigPath("prefix")
    public String prefix = "&e&lBungeeUtilisals &8»";

    @ConfigPath("languages")
    public Map<String, Boolean> languages = createDefaultLanguageMap();




    private HashMap<String, Boolean> createDefaultLanguageMap() {
        HashMap<String, Boolean> map = Maps.newHashMap();
        map.put("english", true);
        map.put("dutch", false);
        return map;
    }
}