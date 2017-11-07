package com.dbsoftwares.bungeeutilisals.api.settings;

import com.google.common.collect.Maps;
import java.util.Map;

public class SettingManager {

    public static Map<SettingType, SettingStorage> settingsMap = Maps.newHashMap();

    public static void loadSettings(SettingStorage storage) {
        settingsMap.put(storage.getType(), storage);
    }

    @SuppressWarnings("unchecked")
    public static <T extends SettingStorage> T getSettings(SettingType type, Class<T> clazz) {
        return (T) settingsMap.get(type);
    }

    public static void reloadAll() {
        settingsMap.values().forEach(SettingStorage::reload);
    }

    public static void reload(SettingType type) {
        settingsMap.get(type).reload();
    }
}