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

package com.dbsoftwares.bungeeutilisals.addon;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.addon.*;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.api.exceptions.AddonException;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.utils.Validate;
import com.dbsoftwares.bungeeutilisals.language.AddonLanguageManager;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.collect.*;
import com.google.gson.Gson;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonManager implements IAddonManager {

    @Getter
    private final File addonsFolder;
    private final IScheduler scheduler;
    private final Map<String, Addon> addons = Maps.newHashMap();
    private final Map<String, AddonDescription> toBeLoaded = Maps.newHashMap();
    private final Multimap<String, Listener> listeners = Multimaps.synchronizedMultimap(HashMultimap.create());
    private final Multimap<String, EventHandler> eventHandlers = Multimaps.synchronizedMultimap(HashMultimap.create());
    private final Multimap<String, Command> commands = Multimaps.synchronizedMultimap(HashMultimap.create());
    private ILanguageManager languageManager;

    @Getter
    private LinkedList<AddonData> allAddons;

    public AddonManager() {
        scheduler = new AddonScheduler();

        addonsFolder = new File(BungeeUtilisals.getInstance().getDataFolder(), "addons");
        if (!addonsFolder.exists()) {
            addonsFolder.mkdir();
        }
        this.languageManager = new AddonLanguageManager(BungeeUtilisals.getInstance());

        loadAllAddons();
    }

    public void loadAllAddons() {
        final HttpRequestFactory factory = new NetHttpTransport().createRequestFactory();
        final Gson gson = new Gson();

        ProxyServer.getInstance().getScheduler().schedule(BungeeUtilisals.getInstance(), () -> {
            try {
                final GenericUrl url = new GenericUrl("https://api.dbsoftwares.eu/plugin/BungeeUtilisals/addons/");
                final HttpRequest request = factory.buildGetRequest(url);
                final Future<HttpResponse> futureResponse = request.executeAsync();

                final HttpResponse response = futureResponse.get();
                if (response.isSuccessStatusCode()) {
                    try (final InputStream input = response.getContent();
                         final InputStreamReader isr = new InputStreamReader(input)) {
                        final AddonData[] addonData = gson.fromJson(isr, AddonData[].class);

                        if (addonData.length > 0) {
                            allAddons = Lists.newLinkedList(Arrays.asList(addonData));
                        }
                    }
                }
            } catch (Exception e) {
                BUCore.getLogger().error("An error occured: ", e);
            }
        }, 0, 1, TimeUnit.MINUTES);
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
                    throw new AddonException("Could not load addon from file " + file.getName(), ex);
                }
            }
        }
    }

    @Override
    public void loadAddons() {
        final Map<AddonDescription, Boolean> addonStatuses = new HashMap<>();
        for (final Map.Entry<String, AddonDescription> entry : toBeLoaded.entrySet()) {
            final AddonDescription addon = entry.getValue();
            try {
                if (!loadAddon(addonStatuses, new ArrayDeque<>(), addon)) {
                    BUCore.getLogger().warn("Could not enable addon {}", entry.getKey());
                }
            } catch (AddonException e) {
                BUCore.getLogger().error("An error occured: ", e);
            }
        }
        toBeLoaded.clear();
    }

    @Override
    public void loadSingleAddon(File addonFile) {
        if (addonFile.isFile() && addonFile.getName().endsWith(".jar")) {
            try (final JarFile jar = new JarFile(addonFile)) {
                final JarEntry entry = jar.getJarEntry("addon.yml");

                Validate.checkNotNull(entry, "Addon must have an addon.yml file");

                try (InputStream in = jar.getInputStream(entry)) {
                    final AddonDescription description = new AddonDescription(IConfiguration.loadYamlConfiguration(in), addonFile);

                    if (!loadAddon(Maps.newHashMap(), new ArrayDeque<>(), description)) {
                        BUCore.getLogger().warn("Could not enable addon {}", addonFile.getName().replace(".jar", ""));
                    }
                }
            } catch (final Exception ex) {
                throw new AddonException("Could not load addon from file " + addonFile.getName(), ex);
            }
        }
    }

    @Override
    public void enableAddons() {
        addons.values().forEach(this::enableAddon);
    }

    @Override
    public void enableAddon(final String addonName) {
        enableAddon(getAddon(addonName));
    }

    private void enableAddon(final Addon addon) {
        if (addon != null) {
            try {
                addon.onEnable();
                BUCore.getLogger().info(
                        "Enabled addon {} version {} by {}",
                        addon.getDescription().getName(), addon.getDescription().getVersion(), addon.getDescription().getAuthor()
                );
            } catch (final Exception t) {
                throw new AddonException("Exception encountered when loading addon: " + addon.getDescription().getName(), t);
            }
        }
    }

    @Override
    public void disableAddons() {
        for (final String addon : Sets.newHashSet(addons.keySet())) {
            try {
                disableAddon(addon);
            } catch (final Exception t) {
                throw new AddonException("Exception encountered when unloading addon: " + addon, t);
            }
        }
    }

    @Override
    public void disableAddon(final String addonName) {
        final Addon addon = getAddon(addonName);

        if (addon != null) {
            addon.onDisable();


            Validate.ifNotNull(scheduler.getTasks(addonName), taskList -> taskList.forEach(IAddonTask::cancel));
            Validate.ifNotNull(eventHandlers.get(addonName), handlerList -> handlerList.forEach(EventHandler::unregister));
            Validate.ifNotNull(listeners.get(addonName), listenerList -> listenerList.forEach(listener ->
                    ProxyServer.getInstance().getPluginManager().unregisterListener(listener)
            ));
            Validate.ifNotNull(commands.get(addonName), commandList -> commandList.forEach(command ->
                    ProxyServer.getInstance().getPluginManager().unregisterCommand(command)
            ));

            if (addon.getClass().getClassLoader() instanceof AddonClassLoader) {
                final AddonClassLoader classLoader = (AddonClassLoader) addon.getClass().getClassLoader();

                AddonClassLoader.getClassLoaders().remove(classLoader);
            }

            addon.getExecutorService().shutdown();
            addons.remove(addonName);
        }
    }

    @Override
    public void reloadAddon(String addonName) {
        final Addon addon = getAddon(addonName);

        if (addon != null) {
            addon.onReload();
        }
    }

    @Override
    public Addon getAddon(final String addonName) {
        return addons.getOrDefault(addonName, null);
    }

    @Override
    public boolean isRegistered(String addonName) {
        return addons.containsKey(addonName);
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
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeUtilisals.getInstance(), listener);
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

    @Override
    public ILanguageManager getLanguageManager() {
        return languageManager;
    }

    private boolean loadAddon(final Map<AddonDescription, Boolean> statuses, final Deque<AddonDescription> dependStack, final AddonDescription description) {
        if (statuses.containsKey(description)) {
            return statuses.get(description);
        }

        final Set<String> dependencies = new HashSet<>(); // set for no duplicates
        dependencies.addAll(description.getRequiredDependencies());
        dependencies.addAll(description.getOptionalDependencies());

        boolean status = true;

        for (String dependency : dependencies) {
            AddonDescription depend = toBeLoaded.get(dependency);
            Boolean dependStatus = (depend != null) ? statuses.get(depend) : Boolean.FALSE;

            if (dependStatus == null) {
                if (dependStack.contains(depend)) {
                    final StringBuilder builder = new StringBuilder();
                    for (AddonDescription element : dependStack) {
                        builder.append(element.getName()).append(" -> ");
                    }
                    builder.append(description.getName()).append(" -> ").append(dependency);
                    BUCore.getLogger().warn("Circular dependency detected: {}", builder);
                    status = false;
                } else {
                    dependStack.push(description);
                    dependStatus = this.loadAddon(statuses, dependStack, depend);
                    dependStack.pop();
                }
            }

            if (dependStatus == Boolean.FALSE && description.getRequiredDependencies().contains(dependency)) {
                BUCore.getLogger().warn("{} (required by {}) is unavailable", dependency, description.getName());
                status = false;
            }

            if (!status) {
                break;
            }
        }

        // do actual loading
        if (status) {
            try {
                final AddonClassLoader loader = new AddonClassLoader(new URL[]{description.getFile().toURI().toURL()});
                final Class<?> main = loader.loadClass(description.getMain());
                final Addon addon = (Addon) main.getDeclaredConstructor().newInstance();

                addon.initialize(ProxyServer.getInstance(), BUCore.getApi(), description);
                addons.put(description.getName(), addon);

                BUCore.getLogger().info("Loaded addon {} version {} by {}", description.getName(), description.getVersion(), description.getAuthor());
            } catch (final Exception e) {
                statuses.put(description, false);
                throw new AddonException("Error enabling addon " + description.getName(), e);
            }
        }

        statuses.put(description, status);
        return status;
    }
}