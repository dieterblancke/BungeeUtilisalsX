package com.dbsoftwares.bungeeutilisals.api.utils;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@UtilityClass
public class ReflectionUtils {

    public Object getHandle(Class<?> clazz, Object o) {
        try {
            return clazz.getMethod("getHandle").invoke(o);
        } catch (Exception e) {
            return null;
        }
    }

    public Object getHandle(Object o) {
        try {
            return getMethod("getHandle", o.getClass()).invoke(o);
        } catch (Exception e) {
            return null;
        }
    }

    public Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean isLoaded(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Method getMethod(String name, Class<?> clazz, Class<?>... paramTypes) {
        for (Method m : clazz.getMethods()) {
            Class<?>[] types = m.getParameterTypes();
            if (m.getName().equals(name) && equalsTypeArray(types, paramTypes)) {
                return m;
            }
        }
        return null;
    }

    public Method getMethod(Class<?> clazz, String name, Class<?>... args) {
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

    public Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getValue(Object instance, Class<?> clazz, String fieldName) throws IllegalArgumentException, IllegalAccessException, SecurityException {
        return getField(clazz, fieldName).get(instance);
    }

    public Object getValue(Object instance, String fieldName) throws IllegalArgumentException, IllegalAccessException, SecurityException {
        return getValue(instance, instance.getClass(), fieldName);
    }

    public void setValue(Object instance, Class<?> clazz, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, SecurityException {
        getField(clazz, fieldName).set(instance, value);
    }

    public void setValue(Object instance, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, SecurityException {
        setValue(instance, instance.getClass(), fieldName, value);
    }

    public Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
        return clazz.getConstructor(parameterTypes);
    }

    private boolean equalsTypeArray(Class<?>[] a, Class<?>[] o) {
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

    private boolean classList(Class<?>[] l1, Class<?>[] l2) {
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
}