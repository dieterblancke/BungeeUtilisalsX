package com.dbsoftwares.bungeeutilisals.bungee.storage.mongodb;

/*
 * Created by DBSoftwares on 12/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import java.util.LinkedHashMap;

public class Mapping<K, V> {

    private boolean nonNull;
    private LinkedHashMap<K, V> map;

    public Mapping() {
        this(false);
    }

    public Mapping(boolean nonNull) {
        this.nonNull = nonNull;
        this.map = new LinkedHashMap<>();
    }

    public Mapping<K, V> append(K key, V value) {
        if (nonNull && (key == null || value == null)) {
            throw new NullPointerException("Could not insert into Mapping: nonNull == true && (key || value == null).");
        }
        map.put(key, value);
        return this;
    }

    public LinkedHashMap<K, V> getMap() {
        return map;
    }
}