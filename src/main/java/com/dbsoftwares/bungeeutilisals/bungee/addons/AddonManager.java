package com.dbsoftwares.bungeeutilisals.bungee.addons;

/*
 * Created by DBSoftwares on 03 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.universal.api.interfaces.BManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.yaml.snakeyaml.error.YAMLException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonManager implements BManager {

    @Getter List<Addon> addons = Lists.newArrayList();
    File folder;

    @Getter private Map<String, AddonClassLoader> loaders = Maps.newLinkedHashMap();
    @Getter private Map<String, Class<?>> classes = Maps.newHashMap();

    @Override
    public void load() {
        folder = new File(BungeeUtilisals.getInstance().getDataFolder(), "addons");

        // Attempting to read Addon list ...
        JsonObject obj = readJson();

        Map<String, File> addons = Maps.newHashMap();
        LinkedHashMap<String, LinkedList<String>> dependencies = Maps.newLinkedHashMap();
        List<String> loadedAddons = Lists.newArrayList();

        for (File file : folder.listFiles()) {
            if (!file.getName().endsWith(".jar")) {
                continue;
            }
            if (isRegistered(file.getName().replace(".jar", ""))) {
                continue;
            }
            System.out.println("[BungeeUtilisals] Trying to register " + file.getName().replace(".jar", ""));

            AddonDescriptionFile description = getAddonDescription(file);
            addons.put(description.getName(), file);

            List<String> depend = description.getDepend();
            if (depend != null && !depend.isEmpty()) {
                dependencies.put(description.getName(), Lists.newLinkedList(depend));
            }

            if (obj != null) {
                if (obj.has(description.getName())) {
                    JsonObject o = obj.get(description.getName()).getAsJsonObject();

                    Integer version = description.getVersion();
                    Integer current = o.get("version").getAsInt();

                    if (current > version) {
                        String link = o.get("link").getAsString();
                        download(description.getName(), link);
                        System.out.println("[BungeeUtilisals] An update for " + description.getName() + " has been installed.");
                        System.out.println("[BungeeUtilisals] Old version: " + version + ", new version: " + current);
                    }
                }
            }
        }
        while (!addons.isEmpty()) {
            Boolean missingDependency = true;
            Iterator<String> addonIterator = addons.keySet().iterator();

            while (addonIterator.hasNext()) {
                String addon = addonIterator.next();

                if (dependencies.containsKey(addon)) {
                    Iterator<String> dependencyIterator = dependencies.get(addon).iterator();

                    while (dependencyIterator.hasNext()) {
                        String dependency = dependencyIterator.next();

                        if (loadedAddons.contains(dependency)) {
                            dependencyIterator.remove();
                        } else if (!addons.containsKey(dependency)) {
                            missingDependency = false;
                            File file = addons.get(addon);
                            addonIterator.remove();
                            dependencies.remove(addon);

                            System.out.println("[BungeeUtilisals] Could not load '" + file.getPath() + "'");
                            break;
                        }
                    }

                    if (dependencies.containsKey(addon) && dependencies.get(addon).isEmpty()) {
                        dependencies.remove(addon);
                    }
                }
                if (!dependencies.containsKey(addon) && addons.containsKey(addon)) {
                    File file = addons.get(addon);
                    addonIterator.remove();
                    missingDependency = false;

                    Addon a = loadAddon(file);
                    try {
                        registerAddon(a);
                    } catch (Exception e) {
                        System.out.println("[BungeeUtilisals] Could not load addon " + a.getName() + ": an error occured!");
                        e.printStackTrace();
                        continue;
                    }
                    loadedAddons.add(addon);
                    System.out.println("[BungeeUtilisals] Registered " + file.getName().replace(".jar", ""));
                }
            }
            if (missingDependency) {
                addonIterator = addons.keySet().iterator();

                while (addonIterator.hasNext()) {
                    String addon = addonIterator.next();

                    if (!dependencies.containsKey(addon)) {
                        missingDependency = false;
                        File file = addons.get(addon);
                        addonIterator.remove();

                        Addon a = loadAddon(file);

                        try {
                            registerAddon(a);
                        } catch (Exception e) {
                            System.out.println("[BungeeUtilisals] Could not load addon " + a.getName() + ": an error occured!");
                            e.printStackTrace();
                            continue;
                        }
                        loadedAddons.add(addon);
                        System.out.println("[BungeeUtilisals] Registered " + file.getName().replace(".jar", ""));
                        break;
                    }
                }
                if (missingDependency) {
                    dependencies.clear();
                    Iterator<File> failedAddonIterator = addons.values().iterator();

                    while (failedAddonIterator.hasNext()) {
                        File file = failedAddonIterator.next();
                        failedAddonIterator.remove();
                    }
                }
            }
        }
    }

    @Override
    public void unload() {
        addons.forEach(Addon::onDisable);
    }

    public Addon loadAddon(File file) {
        return loadAddon(file, null);
    }

    public Addon loadAddon(File file, AddonDescriptionFile description) {
        if (description == null) {
            description = getAddonDescription(file);
        }
        File dataFolder = new File(file.getParentFile(), description.getName());

        for (String pluginName : description.getDepend()) {
            AddonClassLoader current = loaders.get(pluginName);
            if (current == null) {
                System.out.println("Addon " + pluginName + " is missing. Aborting load of " + description.getName() + "!");
                return null;
            }
        }

        AddonClassLoader loader = null;
        try {
            loader = new AddonClassLoader(getClass().getClassLoader(), description, file, dataFolder);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        loaders.put(description.getName(), loader);

        Addon addon = loader.addon;
        addon.setFile(file);
        addon.setFolder(loader.getDataFolder());
        addon.setDescription(loader.getDescription());

        return addon;
    }

    public AddonDescriptionFile getAddonDescription(File file) {
        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry("addon.yml");

            if (entry == null) {
                System.out.println("Could not find addon.yml in " + file.getPath() + "!");
                return null;
            }

            stream = jar.getInputStream(entry);

            return new AddonDescriptionFile(stream);
        } catch (IOException | YAMLException e) {
            System.out.println("Could not load addon located at " + file.getPath() + "!");
            e.printStackTrace();
        } finally {
            try {
                jar.close();
                stream.close();
            } catch (IOException ignore) {}
        }
        return null;
    }

    Class<?> classByName(String name) {
        Class<?> cachedClass = classes.get(name);

        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (String current : loaders.keySet()) {
                AddonClassLoader loader = loaders.get(current);

                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException ignore) { }
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
        }
        return null;
    }

    public File searchAddon(String name) {
        for (File file : folder.listFiles()) {
            if (!file.getName().endsWith(".jar")) {
                continue;
            }
            if (file.getName().contains(name)) {
                return file;
            }
        }
        return null;
    }

    public Addon getAddon(String name) {
        return addons.stream().filter(addon -> addon.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Boolean isRegistered(String name) {
        return getAddon(name) != null;
    }

    public void registerAddon(Addon addon) {
        addons.add(addon);
        addon.onEnable();
    }

    public void unregisterAddon(Addon addon) {
        if (!addons.contains(addon)) {
            return;
        }

        addons.remove(addon);
        addon.onDisable();

        AddonClassLoader loader = loaders.remove(addon.getDescription().getName());
        if (loader == null) {
            return;
        }
        Set<String> names = loader.getClasses();

        for (String name : names) {
            classes.remove(name);
        }
    }

    public Boolean install(String addonname) {
        JsonObject object = readJson();
        if (object == null) {
            return false;
        }
        for (Map.Entry<String, JsonElement> e : object.entrySet()) {
            if (e.getKey().equalsIgnoreCase(addonname)) {
                JsonObject o = e.getValue().getAsJsonObject();

                String name = e.getKey();
                String link = o.get("link").getAsString();
                Integer version = o.get("version").getAsInt();

                if (isRegistered(addonname)) {
                    Addon addon = getAddon(addonname);
                    unregisterAddon(addon);
                }

                File file = download(name, link);
                if (file == null) {
                    return false;
                }

                Addon addon = loadAddon(file);
                if (addon == null) {
                    return false;
                }
                registerAddon(addon);
                return true;
            }
        }
        return false;
    }

    public File download(String file, String link) {
        try {
            URL download = new URL(link);
            BufferedInputStream in = null;
            FileOutputStream fout = null;
            try {
                in = new BufferedInputStream(download.openStream());
                fout = new FileOutputStream("plugins/BungeeUtilisals/addons/" + file + ".jar");

                byte data[] = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) != -1) {
                    fout.write(data, 0, count);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            }
            return new File(BungeeUtilisals.getInstance().getDataFolder(), "addons/" + file + ".jar");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
      TODO: Make MySQL database for Addon list to be retrieved from. If this fails, then use HTML request as backup method. If that fails, simply load Addons found in the Addon folder.

      HikariDataSource source = new HikariDataSource();
      source.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
      source.addDataSourceProperty("serverName", "");
      source.addDataSourceProperty("port", "");
      source.addDataSourceProperty("databaseName", "");
      source.addDataSourceProperty("user", "");
      source.addDataSourceProperty("password", "");

      source.setPoolName("BungeeUtilisals");
      source.setMaximumPoolSize(6);
      source.setMinimumIdle(2);
      source.setConnectionTimeout(2000);
      source.setLeakDetectionThreshold(4000);
     */

    private JsonObject readJson() {
        JsonObject obj = null;

        InputStream stream = null;
        InputStreamReader reader = null;

        try {
            obj = new Gson().fromJson(reader = new InputStreamReader(stream = new URL("http://dbsoftwares.be/plugins/bungeeutilisals/addons/addons.html").openStream()), JsonObject.class);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                stream.close();
                reader.close();
            } catch (IOException ignore) {}
        }
        return obj;
    }
}