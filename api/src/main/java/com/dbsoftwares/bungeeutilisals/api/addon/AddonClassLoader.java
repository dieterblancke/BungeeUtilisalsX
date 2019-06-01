/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.api.addon;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class AddonClassLoader extends URLClassLoader {

    @Getter
    private static final List<AddonClassLoader> classLoaders = Lists.newCopyOnWriteArrayList();

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public AddonClassLoader(URL[] urls) {
        super(urls, BUCore.class.getClassLoader());
        classLoaders.add(this);
    }

    @Override
    public URL getResource(String name) {
        return findResource(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass(name, resolve, true);
    }

    private Class<?> loadClass(String name, boolean resolve, boolean checkOther) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ignored) {
            if (checkOther) {
                for (AddonClassLoader loader : classLoaders) {
                    if (loader != this) {
                        try {
                            return loader.loadClass(name, resolve, false);
                        } catch (ClassNotFoundException ignore) {
                            // ignored
                        }
                    }
                }
            }
        }
        throw new ClassNotFoundException(name);
    }
}
