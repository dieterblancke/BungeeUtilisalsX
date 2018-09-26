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
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;

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

    private final Map<String, List<ScheduledTask>> scheduledTasks = Maps.newConcurrentMap();
    private final Map<String, List<Listener>> listeners = Maps.newConcurrentMap();
    private final Map<String, List<Command>> commands = Maps.newConcurrentMap();

    // TODO: registerListener, registerCommand, registerTask

    @Override
    public void findAddons(final File folder) {
        Validate.checkNotNull(folder, "File is null");
        Validate.ifFalse(folder.isDirectory(), "File must be directory");

        for (final File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                try (final JarFile jar = new JarFile(file)) {
                    final JarEntry entry = jar.getJarEntry("addon.yml");

                    Validate.checkNotNull(entry, "Addon must have an addon.yml file");

                    try (InputStream in = jar.getInputStream(entry)) {
                        final AddonDescription description = new AddonDescription(IConfiguration.loadYamlConfiguration(in), file);

                        toBeLoaded.put(description.getName(), description);
                    }
                } catch (final Exception ex) {
                    BUCore.log(Level.WARNING, "Could not load addon from file " + file, ex);
                }
            }
        }
    }

    @Override
    public void loadAddons() {
        final Map<AddonDescription, Boolean> addonStatuses = new HashMap<>();
        for (final Map.Entry<String, AddonDescription> entry : toBeLoaded.entrySet()) {
            final AddonDescription addon = entry.getValue();
            if (!loadAddon(addonStatuses, new Stack<>(), addon)) {
                BUCore.log(Level.WARNING, "Could not enable addon " + entry.getKey());
            }
        }
        toBeLoaded.clear();
    }

    @Override
    public void enableAddons() {
        addons.values().forEach(addon -> {
            try {
                addon.onEnable();
                BUCore.log(
                        Level.INFO,
                        "Loaded addon " + addon.getDescription().getName() + " version "
                                + addon.getDescription().getVersion() + " by " + addon.getDescription().getAuthor()
                );
            } catch (Throwable t) {
                BUCore.log(Level.WARNING, "Exception encountered when loading addon: " + addon.getDescription().getName(), t);
            }
        });
    }

    @Override
    public void disableAddons() {
        addons.keySet().forEach(this::disableAddon);
    }

    @Override
    public void disableAddon(final String addonName) {
        final Addon addon = getAddon(addonName);

        if (addon != null) {
            addon.onDisable();

            Validate.ifNotNull(scheduledTasks.get(addonName), (tasks) -> tasks.forEach(ScheduledTask::cancel));
            Validate.ifNotNull(listeners.get(addonName), (listeners) -> listeners.forEach(listener -> {
                ProxyServer.getInstance().getPluginManager().unregisterListener(listener);
            }));
            Validate.ifNotNull(commands.get(addonName), (commands) -> commands.forEach(command -> {
                ProxyServer.getInstance().getPluginManager().unregisterCommand(command);
            }));
        }
    }

    @Override
    public Addon getAddon(final String addonName) {
        return addons.getOrDefault(addonName, null);
    }

    @Override
    public Collection<Addon> getAddons() {
        return Collections.unmodifiableCollection(addons.values());
    }

    private boolean loadAddon(final Map<AddonDescription, Boolean> statuses, final Stack<AddonDescription> dependStack, final AddonDescription description) {
        if (statuses.containsKey(description)) {
            return statuses.get(description);
        }

        final Set<String> dependencies = new HashSet<>(); // set for no duplicates
        dependencies.addAll(description.getRequiredDependencies());
        dependencies.addAll(description.getOptionalDependencies());

        boolean status = true;

        for (String dependency : dependencies) {
            AddonDescription depend = toBeLoaded.get(dependency);
            Boolean dependStatus = (depend != null) ? statuses.get(depend) : false;

            if (dependStatus == null) {
                if (dependStack.contains(depend)) {
                    StringBuilder builder = new StringBuilder();
                    for (AddonDescription element : dependStack) {
                        builder.append(element.getName()).append(" -> ");
                    }
                    builder.append(description.getName()).append(" -> ").append(dependency);
                    BUCore.log(Level.WARNING, "Circular dependency detected: " + builder);
                    status = false;
                } else {
                    dependStack.push(description);
                    dependStatus = this.loadAddon(statuses, dependStack, depend);
                    dependStack.pop();
                }
            }

            if (!dependStatus && description.getRequiredDependencies().contains(dependency)) {
                BUCore.log(Level.WARNING, dependency + " (required by " + description.getName() + ") is unavailable");
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

                addon.initialize(ProxyServer.getInstance(), BUCore.getApi(), description);
                addons.put(description.getName(), addon);
                addon.onLoad();

                BUCore.log(Level.INFO, "Loaded addon " + description.getName() + " version " + description.getVersion() + " by " + description.getAuthor());
            } catch (final Throwable t) {
                BUCore.log(Level.WARNING, "Error enabling addon " + description.getName(), t);
                status = false;
            }
        }

        statuses.put(description, status);
        return status;
    }

}
