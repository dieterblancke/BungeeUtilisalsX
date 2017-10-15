package com.dbsoftwares.bungeeutilisals.bungee.addons;

import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.task.builder.ATask;
import com.dbsoftwares.bungeeutilisals.universal.DBCommand;
import com.dbsoftwares.bungeeutilisals.universal.utilities.Utilities;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;

public abstract class Addon {

    @Getter(AccessLevel.PROTECTED) ClassLoader classLoader;
    @Setter @Getter File file;
    @Setter @Getter File folder;
    @Setter @Getter AddonDescriptionFile description;

    public Addon() {
        init();
    }

    protected void init() {
        classLoader = getClass().getClassLoader();
    }

    public File getDataFolder() {
        return folder;
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                BungeeUtilisals.getInstance().getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            BungeeUtilisals.getInstance().getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public static <T extends Addon> T getAddon(Class<T> clazz) {
        if (!Addon.class.isAssignableFrom(clazz)) {
            return null;
        }
        ClassLoader cl = clazz.getClassLoader();
        if (!(cl instanceof AddonClassLoader)) {
            return null;
        }
        Addon addon = ((AddonClassLoader) cl).addon;
        if (addon == null) {
            return null;
        }
        return clazz.cast(addon);
    }

    public String getName() {
        return description.getName();
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract void onReload();

    public Class<?> getClazz() {
        Class<?> clazz = this.getClass();
        if (!clazz.getSuperclass().equals(Addon.class)) {
            clazz = clazz.getSuperclass();
        }
        return clazz;
    }

    private List<DBCommand> commands = Lists.newLinkedList();
    private List<Listener> listeners = Lists.newLinkedList();
    private List<ATask> runnables = Lists.newLinkedList();

    public void loadCommand(DBCommand command) {
        Utilities.loadCommand(command);
        commands.add(command);
    }

    public void unloadCommands() {
        for (DBCommand command : commands) {
            Utilities.unloadCommand(command);
        }
    }

    public void registerListener(Listener listener) {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeUtilisals.getInstance(), listener);
        listeners.add(listener);
    }

    public void unregisterListeners() {
        for (Listener listener : listeners) {
            ProxyServer.getInstance().getPluginManager().unregisterListener(listener);
        }
    }

    public void registerRunnable(ATask task) {
        runnables.add(task);
    }

    public void unregisterRunnables() {
        for (ATask task : runnables) {
            task.task().cancel();
        }
    }
}