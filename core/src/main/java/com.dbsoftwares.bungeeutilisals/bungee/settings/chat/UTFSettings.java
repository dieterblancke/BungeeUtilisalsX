package com.dbsoftwares.bungeeutilisals.bungee.settings.chat;

import com.dbsoftwares.bungeeutilisals.api.settings.SettingStorage;
import com.dbsoftwares.bungeeutilisals.api.settings.SettingType;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import java.util.Map;

@Getter
public class UTFSettings extends SettingStorage {

    Map<String, String> symbols;
    Boolean fancychat;
    String fancyChatPerm;

    public UTFSettings() {
        super(BungeeUtilisals.getInstance(), SettingType.UTFSYMBOLS);
    }

    @Override
    public void reload() {
        super.reloadConfig();

        load();
    }

    @Override
    public void load() {
        Configuration section = getConfig().getSection("symbols");

        symbols = Maps.newHashMap();
        for (String key : section.getKeys()) {
            symbols.put(key, section.getString(key));
        }

        fancychat = getConfig().getBoolean("fancychat.enabled");
        fancyChatPerm = getConfig().getString("fancychat.permission");
    }
}