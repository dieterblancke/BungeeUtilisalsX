package com.dbsoftwares.bungeeutilisals.bungee.library.classloader;

/*
 * Created by DBSoftwares on 16/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.utils.ReflectionUtils;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class LibraryClassLoader {

    private static final Method ADD_URL;

    static {
        ADD_URL = ReflectionUtils.getMethod(URLClassLoader.class, "addURL", URL.class);
    }

    private final URLClassLoader classLoader;

    public LibraryClassLoader(BungeeUtilisals instance) throws IllegalStateException {
        ClassLoader classLoader = instance.getClass().getClassLoader();

        if (classLoader instanceof URLClassLoader) {
            this.classLoader = (URLClassLoader) classLoader;
        } else {
            throw new IllegalStateException("Plugin ClassLoader is not instance of URLClassLoader");
        }
    }

    public void loadLibrary(URL url) {
        try {
            ADD_URL.invoke(this.classLoader, url);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void loadLibrary(File file) {
        try {
            loadLibrary(file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}