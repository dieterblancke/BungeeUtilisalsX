package com.dbsoftwares.bungeeutilisals.api.addon;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class AddonClassLoader extends URLClassLoader {

    private static final Set<AddonClassLoader> allLoaders = new CopyOnWriteArraySet<>();

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public AddonClassLoader(URL[] urls) {
        super(urls);
        allLoaders.add(this);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass(name, resolve, true);
    }

    private Class<?> loadClass(String name, boolean resolve, boolean checkOther) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ignored) {
        }
        if (checkOther) {
            for (AddonClassLoader loader : allLoaders) {
                if (loader != this) {
                    try {
                        return loader.loadClass(name, resolve, false);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            }
        }
        throw new ClassNotFoundException(name);
    }
}
