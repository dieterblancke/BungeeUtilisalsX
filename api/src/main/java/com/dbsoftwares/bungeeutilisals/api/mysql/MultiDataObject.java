package com.dbsoftwares.bungeeutilisals.api.mysql;

/*
 * Created by DBSoftwares on 04/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.LinkedHashMap;

public class MultiDataObject {

    @Getter
    LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();

    public Object getValue(String column) {
        return map.get(column);
    }

    @SuppressWarnings("unchecked")
    public <T> T getCastedValue(String column) {
        return (T) map.get(column);
    }
}