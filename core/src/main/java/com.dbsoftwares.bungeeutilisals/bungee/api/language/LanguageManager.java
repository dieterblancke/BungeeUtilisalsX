package com.dbsoftwares.bungeeutilisals.bungee.api.language;

import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.configuration.ISection;
import com.dbsoftwares.bungeeutilisals.api.configuration.json.JsonConfiguration;
import com.dbsoftwares.bungeeutilisals.api.configuration.yaml.YamlConfiguration;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileStorageType;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LanguageManager implements ILanguageManager {

    @Getter Map<Plugin, File> plugins = Maps.newHashMap();
    @Getter Map<Plugin, FileStorageType> fileTypes = Maps.newHashMap();
    @Getter Map<File, IConfiguration> configurations = Maps.newHashMap();
    @Getter List<Language> languages = Lists.newArrayList();

    public LanguageManager(BungeeUtilisals plugin) {
        ISection section = BungeeUtilisals.getConfiguration(FileLocation.LANGUAGES_CONFIG).getSection("languages");

        for (String key : section.getKeys()) {
            languages.add(new Language(key, section.getBoolean(key + ".default")));
        }

        addPlugin(plugin, new File(plugin.getDataFolder(), "languages"), FileStorageType.JSON);
        loadLanguages(plugin);
    }

    @Override
    public Optional<Language> getDefaultLanguage() {
        Optional<Language> optional = languages.stream().filter(Language::isDefault).findFirst();
        if (!optional.isPresent() && languages.size() > 0) {
            return Optional.ofNullable(languages.get(0));
        }
        return optional;
    }

    @Override
    public Optional<Language> getLanguage(String language) {
        return languages.stream().filter(lang -> lang.getName().equalsIgnoreCase(language)).findFirst();
    }

    @Override
    public void addPlugin(Plugin plugin, File folder, FileStorageType type) {
        plugins.put(plugin, folder);
        fileTypes.put(plugin, type);
    }

    @Override
    public void loadLanguages(Plugin plugin) {
        if (!plugins.containsKey(plugin)) {
            throw new RuntimeException("The plugin " + plugin.getDescription().getName() + " is not registered!");
        }
        File folder = plugins.get(plugin);

        for (Language language : languages) {
            String name = language.getName();
            File lang;

            if (fileTypes.get(plugin).equals(FileStorageType.JSON)) {
                lang = loadResource(plugin, "languages/" + name + ".json", new File(folder, name + ".json"));
            } else {
                lang = loadResource(plugin, "languages/" + name + ".yml", new File(folder, name + ".yml"));
            }

            if (!lang.exists()) {
                continue;
            }
            try {
                IConfiguration configuration;

                if (fileTypes.get(plugin).equals(FileStorageType.JSON)) {
                    configuration = IConfiguration.loadConfiguration(JsonConfiguration.class, lang);

                    configuration.copyDefaults(IConfiguration.loadConfiguration(JsonConfiguration.class,
                            plugin.getResourceAsStream("languages/" + name + ".json")));
                } else {
                    configuration = IConfiguration.loadConfiguration(YamlConfiguration.class, lang);

                    configuration.copyDefaults(IConfiguration.loadConfiguration(YamlConfiguration.class,
                            plugin.getResourceAsStream("languages/" + name + ".yml")));
                }

                configurations.put(lang, configuration);
                saveLanguage(plugin, language);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IConfiguration getLanguageConfiguration(Plugin plugin, User user) {
        IConfiguration config = null;
        if (user != null) {
            config = getConfig(plugin, user.getLanguage());
        }
        if (config == null) {
            config = getConfig(plugin, getDefaultLanguage().get());
        }
        return config;
    }

    @Override
    public IConfiguration getLanguageConfiguration(Plugin plugin, ProxiedPlayer player) {
        return getLanguageConfiguration(plugin, BungeeUtilisals.getApi().getUser(player).orElse(null));
    }

    @Override
    public IConfiguration getLanguageConfiguration(Plugin plugin, CommandSender sender) {
        return getLanguageConfiguration(plugin, BungeeUtilisals.getApi().getUser(sender.getName()).orElse(null));
    }

    @Override
    public File getFile(Plugin plugin, Language language) {
        if (!plugins.containsKey(plugin)) {
            throw new RuntimeException("The plugin " + plugin.getDescription().getName() + " is not registered!");
        }
        return new File(plugins.get(plugin), language.getName() + ".json");
    }

    @Override
    public IConfiguration getConfig(Plugin plugin, Language language) {
        if (!plugins.containsKey(plugin)) {
            throw new RuntimeException("The plugin " + plugin.getDescription().getName() + " is not registered!");
        }
        File lang = getFile(plugin, language);

        if (!configurations.containsKey(lang)) {
            BungeeUtilisals.getLog().info("The plugin " + plugin.getDescription().getName() + " did not register the language " + language.getName() + " yet!");

            File deflang = getFile(plugin, getDefaultLanguage().get());
            if (configurations.containsKey(deflang)) {
                return configurations.get(deflang);
            }
            return null;
        }
        return configurations.get(lang);
    }

    @Override
    public Boolean isRegistered(Plugin plugin, Language language) {
        if (!plugins.containsKey(plugin)) {
            throw new RuntimeException("The plugin " + plugin.getDescription().getName() + " is not registered!");
        }
        return getConfig(plugin, language) != null;
    }

    @Override
    public Boolean saveLanguage(Plugin plugin, Language language) {
        if (!plugins.containsKey(plugin)) {
            throw new RuntimeException("The plugin " + plugin.getDescription().getName() + " is not registered!");
        }
        File lang = getFile(plugin, language);
        IConfiguration config = configurations.get(lang);

        try {
            config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Boolean reloadConfig(Plugin plugin, Language language) {
        if (!plugins.containsKey(plugin)) {
            throw new RuntimeException("The plugin " + plugin.getDescription().getName() + " is not registered!");
        }
        File lang = getFile(plugin, language);
        IConfiguration config = configurations.get(lang);
        try {
            config.reload();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private File loadResource(Plugin plugin, String source, File target) {
        if (!plugins.containsKey(plugin)) {
            throw new RuntimeException("The plugin " + plugin.getDescription().getName() + " is not registered!");
        }
        File folder = plugins.get(plugin);
        if (!folder.exists()) {
            folder.mkdir();
        }
        try {
            if (!target.exists()) {
                target.createNewFile();
                try (InputStream in = plugin.getResourceAsStream(source); OutputStream out = new FileOutputStream(target)) {
                    if (in == null) {
                        BungeeUtilisals.getLog().info("Didn't found default configuration for language " +
                                source.replace("languages/", "").replace(".json", "") +
                                " for plugin " + plugin.getDescription().getName());
                        return null;
                    }
                    ByteStreams.copy(in, out);
                    BungeeUtilisals.getLog().info("Loading default configuration for language "
                            + source.replace("languages/", "").replace(".json", "") + " for plugin "
                            + plugin.getDescription().getName());

                    in.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;
    }
}