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

package com.dbsoftwares.bungeeutilisals.storage.mongodb;

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
            return this;
        }
        map.put(key, value);
        return this;
    }

    public LinkedHashMap<K, V> getMap() {
        return map;
    }
}