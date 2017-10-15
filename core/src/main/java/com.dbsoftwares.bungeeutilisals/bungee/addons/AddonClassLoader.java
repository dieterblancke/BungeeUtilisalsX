package com.dbsoftwares.bungeeutilisals.bungee.addons;

/*
 * Created by DBSoftwares on 10 februari 2017
 * Developer: Dieter Blancke
 * Project: CMS
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.bungeeutilisals.universal.api.interfaces.BManager;
import com.google.common.collect.Maps;
import lombok.Getter;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;

final class AddonClassLoader extends URLClassLoader {

    private final Map<String, Class<?>> classes = Maps.newHashMap();
    @Getter private final AddonDescriptionFile description;
    @Getter private final File file;
    @Getter private final File dataFolder;
    protected Addon addon;

    AddonClassLoader(final ClassLoader parent, final AddonDescriptionFile description, File file, File dataFolder) throws MalformedURLException {
        super(new URL[] {file.toURI().toURL()}, parent);


        this.description = description;
        this.file = file;
        this.dataFolder = dataFolder;

        try {
            Class<? extends Addon> clazz = Class.forName(description.getMain(), true, this).asSubclass(Addon.class);

            this.addon = clazz.newInstance();
        } catch (Throwable throwable) {
            System.out.println("Could not load addon " + description.getName() + " (main class: " + description.getMain() + ").");
            throwable.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    Class<?> findClass(String name, boolean check) throws ClassNotFoundException {
        Class<?> result = classes.get(name);

        if (result == null) {
            if (check) {
                result = BManager.get(AddonManager.class).classByName(name);
            }

            if (result == null) {
                result = super.findClass(name);

                if (result != null) {
                    BManager.get(AddonManager.class).getClasses().put(name, result);
                }
            }

            classes.put(name, result);
        }

        return result;
    }

    Set<String> getClasses() {
        return classes.keySet();
    }
}