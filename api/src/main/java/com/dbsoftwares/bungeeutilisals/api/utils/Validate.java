package com.dbsoftwares.bungeeutilisals.api.utils;

/*
 * Created by DBSoftwares on 04 01 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

import java.util.function.Consumer;

public class Validate {

    public static void notNull(Object toCheck, String error) {
        if (toCheck == null) {
            throw new RuntimeException(error);
        }
    }

    public static <T> void ifNotNull(T toCheck, Consumer<T> consumer) {
        if (toCheck != null) {
            consumer.accept(toCheck);
        }
    }
}