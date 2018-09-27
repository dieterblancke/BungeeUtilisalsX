package com.dbsoftwares.bungeeutilisals.addon;

/*
 * Created by DBSoftwares on 26/09/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.addon.*;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.api.utils.Validate;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.ReflectionUtils;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class AddonManager implements IAddonManager {

    @Getter
    private final File addonsFolder;
    private final IScheduler scheduler;
    private final Map<String, Addon> addons = Maps.newHashMap();
    private final Map<String, AddonDescription> toBeLoaded = Maps.newHashMap();

    private final Multimap<String, Listener> listeners = Multimaps.synchronizedMultimap(HashMultimap.create());
    private final Multimap<String, EventHandler> eventHandlers = Multimaps.synchronizedMultimap(HashMultimap.create());
    private final Multimap<String, Command> commands = Multimaps.synchronizedMultimap(HashMultimap.create());

    public AddonManager() {
        scheduler = new AddonScheduler();

        addonsFolder = new File(BungeeUtilisals.getInstance().getDataFolder(), "addons");
        if (!addonsFolder.exists()) {
            addonsFolder.mkdir();
        }

        findAddons(addonsFolder);
        loadAddons();
        enableAddons();
    }

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
            } catch (final Throwable t) {
                BUCore.log(Level.WARNING, "Exception encountered when loading addon: " + addon.getDescription().getName(), t);
            }
        });
    }

    @Override
    public void disableAddons() {
        for (final String addon : addons.keySet()) {
            try {
                disableAddon(addon);
            } catch (final Throwable t) {
                BUCore.log(Level.WARNING, "Exception encountered when unloading addon: " + addon, t);
            }
        }
        addons.keySet().forEach(this::disableAddon);
    }

    @Override
    public void disableAddon(final String addonName) {
        final Addon addon = getAddon(addonName);

        if (addon != null) {
            addon.onDisable();

            Validate.ifNotNull(scheduler.getTasks(addonName), (tasks) -> tasks.forEach(IAddonTask::cancel));
            Validate.ifNotNull(eventHandlers.get(addonName), (handlers) -> handlers.forEach(EventHandler::unregister));
            Validate.ifNotNull(listeners.get(addonName), (listeners) -> listeners.forEach(listener -> {
                ProxyServer.getInstance().getPluginManager().unregisterListener(listener);
            }));
            Validate.ifNotNull(commands.get(addonName), (commands) -> commands.forEach(command -> {
                ProxyServer.getInstance().getPluginManager().unregisterCommand(command);
            }));

            addon.getExecutorService().shutdown();
            addons.remove(addonName);
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

    @Override
    public IScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public void registerListener(final Addon addon, final Listener listener) {
        this.listeners.put(addon.getDescription().getName(), listener);
    }

    @Override
    public void registerEventHandler(final Addon addon, final EventHandler handler) {
        this.eventHandlers.put(addon.getDescription().getName(), handler);
    }

    @Override
    public void registerCommand(final Addon addon, final Command command) {
        this.commands.put(addon.getDescription().getName(), command);
    }

    @Override
    public Collection<Listener> getListeners(final String addonName) {
        return Collections.unmodifiableCollection(listeners.get(addonName));
    }

    @Override
    public Collection<EventHandler> getEventHandlers(final String addonName) {
        return Collections.unmodifiableCollection(eventHandlers.get(addonName));
    }

    @Override
    public Collection<Command> getCommands(final String addonName) {
        return Collections.unmodifiableCollection(commands.get(addonName));
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
                    final StringBuilder builder = new StringBuilder();
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
                final URLClassLoader loader = new AddonClassLoader(new URL[]{description.getFile().toURI().toURL()});
                final Class<?> main = loader.loadClass(description.getMain());
                final Addon addon = (Addon) main.getDeclaredConstructor().newInstance();

                initialize(addon, ProxyServer.getInstance(), BUCore.getApi(), description);
                addons.put(description.getName(), addon);

                BUCore.log(Level.INFO, "Loaded addon " + description.getName() + " version " + description.getVersion() + " by " + description.getAuthor());
            } catch (final Throwable t) {
                BUCore.log(Level.WARNING, "Error enabling addon " + description.getName(), t);
                status = false;
            }
        }

        statuses.put(description, status);
        return status;
    }

    private void initialize(final Addon addon, final ProxyServer proxy, final BUAPI api, final AddonDescription desc) {
        try {
            final Method initialize = ReflectionUtils.getMethod(
                    addon.getClass(), "initialize", ProxyServer.class, BUAPI.class, AddonDescription.class
            );

            initialize.invoke(addon, proxy, api, desc);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
