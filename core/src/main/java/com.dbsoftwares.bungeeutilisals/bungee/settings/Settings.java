package com.dbsoftwares.bungeeutilisals.bungee.settings;

import com.dbsoftwares.bungeeutilisals.api.utils.file.FileUtils;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Settings {

    private BungeeUtilisals plugin = BungeeUtilisals.getInstance();

    public Settings() {
        ANTISWEAR_PATTERNS = Lists.newArrayList();
        for (String word : ANTISWEAR_WORDS.get()) {
            StringBuilder builder = new StringBuilder("\\b(");

            for (char o : word.toCharArray()) {
                builder.append(o);
                builder.append("+(\\W|\\d\\_)*");
            }
            builder.append(")\\b");

            ANTISWEAR_PATTERNS.add(Pattern.compile(builder.toString()));
        }

        if (configurations.containsKey(utfSymbolsPath)) {
            Configuration configuration = configurations.get(utfSymbolsPath);
            Configuration section = configuration.getSection("symbols.symbols");

            for (String key : section.getKeys()) {
                UTFSYMBOLS_SYMBOLS.put(key, section.getString(key));
            }
        }
    }

    private Map<String, Configuration> configurations = Maps.newHashMap();

    private String mysqlPath = "mysql.yml";
    private String punishmentPath = "punishments/punishments.yml";
    private String utfSymbolsPath = "chat/utfsymbols.yml";
    private String antiSwearPath = "chat/antiswear.yml";

    // MySQL Settings
    public Setting<String> MYSQL_HOST = new Setting<>(mysqlPath, "hostname", "host");
    public Setting<Integer> MYSQL_PORT = new Setting<>(mysqlPath, "port", 3306);
    public Setting<String> MYSQL_DATABASE = new Setting<>(mysqlPath, "database", "database");
    public Setting<String> MYSQL_USERNAME = new Setting<>(mysqlPath, "username", "username");
    public Setting<String> MYSQL_PASSWORD = new Setting<>(mysqlPath, "password", "password");

    // UTF Symbols Settings
    public Setting<Boolean> UTFSYMBOLS_ENABLED = new Setting<>(utfSymbolsPath, "symbols.enabled", true);
    public Setting<String> UTFSYMBOLS_PERMISSION = new Setting<>(utfSymbolsPath, "symbols.permission", "bungeeutilisals.chat.symbols");
    public Map<String, String> UTFSYMBOLS_SYMBOLS = Maps.newHashMap();
    public Setting<Boolean> FANCYCHAT_ENABLED = new Setting<>(utfSymbolsPath, "fancychat.enabled", false);
    public Setting<String> FANCYCHAT_PERMISSION = new Setting<>(utfSymbolsPath, "fancychat.permission", "bungeeutilisals.chat.fancy");

    // AntiSwear Settings
    public Setting<Boolean> ANTISWEAR_ENABLED = new Setting<>(antiSwearPath, "enabled", true);
    public Setting<Boolean> ANTISWEAR_CANCEL = new Setting<>(antiSwearPath, "cancel", true);
    public Setting<String> ANTISWEAR_REPLACEMENT = new Setting<>(antiSwearPath, "replace", "****");
    public Setting<List<String>> ANTISWEAR_WORDS = new Setting<>(antiSwearPath, "words", Lists.newArrayList());
    public List<Pattern> ANTISWEAR_PATTERNS;

    // General Punishment settings
    public Setting<Boolean> PUNISHMENT_ENABLED = new Setting<>(punishmentPath, "enabled", false);
    public Setting<String> PUNISHMENT_TABLE = new Setting<>(punishmentPath, "tables.punishments", "bu_punishments");
    public Setting<String> PUNISHMENT_SOFT_TABLE = new Setting<>(punishmentPath, "tables.soft-punishments", "bu_soft_punishments");

    public void reloadAll() {
        Set<String> set = Sets.newHashSet(configurations.keySet());

        for (String path : set) {
            File file = new File(plugin.getDataFolder(), path);

            try {
                configurations.put(path, ConfigurationProvider.getProvider(YamlConfiguration.class).load(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Field field : getClass().getFields()) {
            try {
                Object object = field.get(this);

                if (!(object instanceof Setting)) {
                    continue;
                }
                Setting setting = (Setting) object;
                setting.reload();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void reloadFromFile(String path) {
        if (!configurations.containsKey(path)) {
            return;
        }
        File file = new File(plugin.getDataFolder(), path);

        try {
            configurations.put(path, ConfigurationProvider.getProvider(YamlConfiguration.class).load(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Field field : getClass().getFields()) {
            try {
                Object object = field.get(this);

                if (!(object instanceof Setting)) {
                    continue;
                }
                Setting setting = (Setting) object;
                if (setting.filePath.equals(path)) {
                    setting.reload();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public class Setting<T> {

        private String filePath;
        private File file;
        private String path;
        private T value;

        @SuppressWarnings("unchecked") // to avoid the warning, shouldn't happen to miscast.
        public Setting(String filePath, String path, T value) {
            this.filePath = filePath;
            this.file = new File(plugin.getDataFolder(), filePath);

            if (!file.exists()) {
                FileUtils.createDefaultFile(plugin, filePath, file, true);
            }

            this.path = path;

            reload();
        }

        public void setAndSave(T value) {
            set(value);
            save();
        }

        public void set(T value) {
            this.value = value;
            configurations.get(filePath).set(path, value);
        }

        public T get() {
            return value;
        }

        public T getAndSet(T value) {
            T v = get();
            set(value);
            return v;
        }

        public T getSetAndSave(T value) {
            T v = getAndSet(value);
            save();
            return v;
        }

        @SuppressWarnings("unchecked")
        public void reload() {
            Configuration configuration;
            if (!configurations.containsKey(filePath)) {
                try {
                    configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                    configurations.put(filePath, configuration);
                } catch (IOException e) {
                    e.printStackTrace();
                    configuration = null;
                }
            } else {
                configuration = configurations.get(filePath);
            }
            if (configuration != null && configuration.contains(path)) {
                this.value = (T) configuration.get(path);
            } else {
                setAndSave(value);
            }
        }

        public void save() {
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configurations.get(filePath), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}