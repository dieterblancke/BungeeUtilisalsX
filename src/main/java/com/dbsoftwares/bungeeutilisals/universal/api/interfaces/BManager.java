package com.dbsoftwares.bungeeutilisals.universal.api.interfaces;

/*
 * Created by DBSoftwares on 03 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

import com.google.common.collect.Lists;

import java.util.List;

public interface BManager {

    List<BManager> managers = Lists.newArrayList();

    @SuppressWarnings("unchecked")
    static <T extends BManager> T initialize(Class<T> clazz) {
        try {
            BManager manager = clazz.newInstance();

            managers.add(manager);
            manager.load();

            return (T) manager;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static <T extends BManager> T get(Class<T> clazz) {
        for(BManager manager : managers) {
            if (manager.getClass().equals(clazz)) {
                return (T) manager;
            }
        }
        return null;
    }

    void load();

    void unload();
}