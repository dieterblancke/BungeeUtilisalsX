/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.language;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.configuration.api.FileStorageType;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;

public class PluginLanguageManager extends AbstractLanguageManager {

    public PluginLanguageManager(BungeeUtilisals plugin) {
        super(plugin);
    }

    @Override
    public void loadLanguages(String pluginName) {
        Plugin plugin = ProxyServer.getInstance().getPluginManager().getPlugin(pluginName);
        if (plugin == null) {
            return;
        }
        File folder = plugins.get(pluginName);

        for (Language language : languages) {
            String name = language.getName();
            File lang;

            if (fileTypes.get(pluginName).equals(FileStorageType.JSON)) {
                lang = loadResource(pluginName, "languages/" + name + ".json", new File(folder, name + ".json"));
            } else {
                lang = loadResource(pluginName, "languages/" + name + ".yml", new File(folder, name + ".yml"));
            }

            if (!lang.exists()) {
                continue;
            }
            try {
                IConfiguration configuration;

                if (fileTypes.get(pluginName).equals(FileStorageType.JSON)) {
                    configuration = IConfiguration.loadJsonConfiguration(lang);
                    configuration.copyDefaults(IConfiguration.loadJsonConfiguration(plugin.getResourceAsStream("languages/" + name + ".json")));
                } else {
                    configuration = IConfiguration.loadYamlConfiguration(lang);
                    configuration.copyDefaults(IConfiguration.loadYamlConfiguration(plugin.getResourceAsStream("languages/" + name + ".yml")));
                }

                configurations.put(lang, configuration);
                saveLanguage(pluginName, language);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected File loadResource(String pluginName, String source, File target) {
        Plugin plugin = ProxyServer.getInstance().getPluginManager().getPlugin(pluginName);
        if (plugin == null) {
            return target;
        }
        File folder = plugins.get(pluginName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        try {
            if (!target.exists()) {
                target.createNewFile();
                try (InputStream in = plugin.getResourceAsStream(source); OutputStream out = new FileOutputStream(target)) {
                    if (in == null) {
                        BUCore.log("Didn't found default configuration for language " +
                                source.replace("languages/", "").replace(".json", "") +
                                " for addon " + pluginName);
                        return null;
                    }
                    ByteStreams.copy(in, out);
                    BUCore.log("Loading default configuration for language "
                            + source.replace("languages/", "").replace(".json", "") + " for addon "
                            + pluginName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;
    }
}