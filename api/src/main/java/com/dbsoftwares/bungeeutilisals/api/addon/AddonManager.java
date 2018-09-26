package com.dbsoftwares.bungeeutilisals.api.addon;

/*
 * Created by DBSoftwares on 26/09/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.utils.Validate;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.ProxyServer;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class AddonManager implements IAddonManager {

    private final Map<String, Addon> addons = Maps.newHashMap();
    private final Map<String, AddonDescription> toBeLoaded = Maps.newHashMap();

    @Override
    public void findAddons(File folder) {
        Validate.checkNotNull(folder, "File is null");
        Validate.ifFalse(folder.isDirectory(), "File must be directory");

        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                try (JarFile jar = new JarFile(file)) {
                    JarEntry entry = jar.getJarEntry("addon.yml");

                    Validate.checkNotNull(entry, "Addon must have an addon.yml file");

                    try (InputStream in = jar.getInputStream(entry)) {
                        AddonDescription description = new AddonDescription(IConfiguration.loadYamlConfiguration(in), file);

                        toBeLoaded.put(description.getName(), description);
                    }
                } catch (Exception ex) {
                    BUCore.log(Level.WARNING, "Could not load addon from file " + file, ex);
                }
            }
        }
    }

    @Override
    public void loadAddons() {
        Map<AddonDescription, Boolean> addonStatuses = new HashMap<>();
        for (Map.Entry<String, AddonDescription> entry : toBeLoaded.entrySet()) {
            AddonDescription addon = entry.getValue();
            if (!enableaddon(addonStatuses, new Stack<>(), addon)) {
                BUCore.log(Level.WARNING, "Failed to enable " + entry.getKey());
            }
        }
        toBeLoaded.clear();
    }

    private boolean enableaddon(Map<AddonDescription, Boolean> addonStatuses, Stack<AddonDescription> dependStack, AddonDescription description) {
        if (addonStatuses.containsKey(description)) {
            return addonStatuses.get(description);
        }

        // combine all dependencies for 'for loop'
        Set<String> dependencies = new HashSet<>();
        dependencies.addAll(description.getRequiredDependencies());
        dependencies.addAll(description.getOptionalDependencies());

        // success status
        boolean status = true;

        // try to load dependencies first
        for (String dependName : dependencies) {
            AddonDescription depend = toBeLoaded.get(dependName);
            Boolean dependStatus = (depend != null) ? addonStatuses.get(depend) : false;

            if (dependStatus == null) {
                if (dependStack.contains(depend)) {
                    StringBuilder dependencyGraph = new StringBuilder();
                    for (AddonDescription element : dependStack) {
                        dependencyGraph.append(element.getName()).append(" -> ");
                    }
                    dependencyGraph.append(description.getName()).append(" -> ").append(dependName);
                    BUCore.log(Level.WARNING, "Circular dependency detected: " + dependencyGraph);
                    status = false;
                } else {
                    dependStack.push(description);
                    dependStatus = this.enableaddon(addonStatuses, dependStack, depend);
                    dependStack.pop();
                }
            }

            if (!dependStatus && description.getRequiredDependencies().contains(dependName)) {
                BUCore.log(Level.WARNING, dependName + " (required by " + description.getName() + ") is unavailable");
                status = false;
            }

            if (!status) {
                break;
            }
        }

        // do actual loading
        if (status) {
            try {
                URLClassLoader loader = new AddonClassLoader(new URL[]{description.getFile().toURI().toURL()});
                Class<?> main = loader.loadClass(description.getMain());
                Addon addon = (Addon) main.getDeclaredConstructor().newInstance();

                addon.init(ProxyServer.getInstance(), BUCore.getApi(), description);
                addons.put(description.getName(), addon);
                addon.onLoad();

                BUCore.log(Level.INFO, "Loaded addon " + description.getName() + " version " + description.getVersion() + " by " + description.getAuthor());
            } catch (Throwable t) {
                BUCore.log(Level.WARNING, "Error enabling addon " + description.getName(), t);
            }
        }

        addonStatuses.put(description, status);
        return status;
    }

}
