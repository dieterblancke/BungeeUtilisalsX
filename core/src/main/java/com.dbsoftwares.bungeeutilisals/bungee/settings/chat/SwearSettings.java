package com.dbsoftwares.bungeeutilisals.bungee.settings.chat;

import com.dbsoftwares.bungeeutilisals.api.settings.SettingStorage;
import com.dbsoftwares.bungeeutilisals.api.settings.SettingType;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.Map;

@Getter
public class SwearSettings extends SettingStorage {

    Boolean enabled;
    List<String> swearWords;

    public SwearSettings() {
        super(BungeeUtilisals.getInstance(), SettingType.ANTISWEAR);
    }

    @Override
    public void reload() {
        super.reloadConfig();

        load();
    }

    @Override
    public void load() {
        Configuration section = getConfig().getSection("symbols");

        enabled = getConfig().getBoolean("enabled");
        swearWords = getConfig().getStringList("words");
    }
}