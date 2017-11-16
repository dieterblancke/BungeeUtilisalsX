package com.dbsoftwares.bungeeutilisals.api.settings;

import com.dbsoftwares.bungeeutilisals.api.utils.file.FileUtils;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public abstract class SettingStorage {

    private static SettingStorage instance;
    SettingType type;
    File file;
    Configuration config;

    public SettingStorage(Plugin plugin, SettingType type) {
        instance = this;
        this.type = type;
        this.file = new File(plugin.getDataFolder(), type.getPath());

        if (!file.exists()) {
            FileUtils.createDefaultFile(plugin, type.getPath(), file, true);
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        load();
    }

    public abstract void reload();

    public abstract void load();

    public void reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}