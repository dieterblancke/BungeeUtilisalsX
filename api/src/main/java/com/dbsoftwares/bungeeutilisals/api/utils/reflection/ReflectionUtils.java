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

package com.dbsoftwares.bungeeutilisals.api.utils.reflection;

import com.dbsoftwares.bungeeutilisals.api.BUCore;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

    public static Object getHandle(Class<?> clazz, Object o) {
        try {
            return clazz.getMethod("getHandle").invoke(o);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getHandle(Object o) {
        try {
            final Method getHandle = getMethod("getHandle", o.getClass());

            if (getHandle == null) {
                return null;
            } else {
                return getHandle.invoke(o);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            BUCore.getLogger().error("An error occured: ", e);
        }
        return null;
    }

    public static Boolean isLoaded(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Method getMethod(String name, Class<?> clazz, Class<?>... paramTypes) {
        for (Method m : clazz.getMethods()) {
            Class<?>[] types = m.getParameterTypes();
            if (m.getName().equals(name) && equalsTypeArray(types, paramTypes)) {
                return m;
            }
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(name) && (args.length == 0 || classList(args, m.getParameterTypes()))) {
                m.setAccessible(true);
                return m;
            }
        }
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name) && (args.length == 0 || classList(args, m.getParameterTypes()))) {
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }

    public static Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            BUCore.getLogger().error("An error occured: ", e);
        }
        return null;
    }

    public static Object getValue(Object instance, Class<?> clazz, String fieldName) throws IllegalArgumentException, IllegalAccessException, SecurityException {
        final Field field = getField(clazz, fieldName);

        if (field == null) {
            return null;
        } else {
            return field.get(instance);
        }
    }

    public static Object getValue(Object instance, String fieldName) throws IllegalArgumentException, IllegalAccessException, SecurityException {
        return getValue(instance, instance.getClass(), fieldName);
    }

    public static void setValue(Object instance, Class<?> clazz, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, SecurityException {
        final Field field = getField(clazz, fieldName);

        if (field != null) {
            field.set(instance, value);
        }
    }

    public static void setValue(Object instance, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, SecurityException {
        setValue(instance, instance.getClass(), fieldName, value);
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
        return clazz.getConstructor(parameterTypes);
    }

    private static boolean equalsTypeArray(Class<?>[] a, Class<?>[] o) {
        if (a.length != o.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (!a[i].equals(o[i]) && !a[i].isAssignableFrom(o[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean classList(Class<?>[] l1, Class<?>[] l2) {
        boolean equal = true;
        if (l1.length != l2.length) {
            return false;
        }
        for (int i = 0; i < l1.length; i++) {
            if (l1[i] != l2[i]) {
                equal = false;
                break;
            }
        }
        return equal;
    }

    public static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2);
        }
        final int dotPos = version.indexOf('.');
        final int dashPos = version.indexOf('-');
        return Integer.parseInt(version.substring(0, dotPos > -1 ? dotPos : dashPos > -1 ? dashPos : 1));
    }
}