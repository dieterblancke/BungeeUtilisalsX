package com.dbsoftwares.bungeeutilisals.api.configuration;

import com.dbsoftwares.bungeeutilisals.api.utils.ReflectionUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileStorageType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

public interface IConfiguration extends ISection {

    /**
     * Loads in a IConfiguration object from file.
     * @param file The file that has to be read.
     * @return A new IConfiguration instance for the given file.
     */
    static IConfiguration loadJsonConfiguration(File file) {
        return loadConfiguration(FileStorageType.JSON, file);
    }

    /**
     * Loads in a IConfiguration object from InputStream.
     * @param input The stream that has to be read.
     * @return A new IConfiguration instance for the given stream.
     */
    static IConfiguration loadJsonConfiguration(InputStream input) {
        return loadConfiguration(FileStorageType.JSON, input);
    }

    /**
     * Loads in a IConfiguration object from file.
     * @param file The file that has to be read.
     * @return A new IConfiguration instance for the given file.
     */
    static IConfiguration loadYamlConfiguration(File file) {
        return loadConfiguration(FileStorageType.YAML, file);
    }

    /**
     * Loads in a IConfiguration object from InputStream.
     * @param input The stream that has to be read.
     * @return A new IConfiguration instance for the given stream.
     */
    static IConfiguration loadYamlConfiguration(InputStream input) {
        return loadConfiguration(FileStorageType.YAML, input);
    }

    /**
     * Loads in a IConfiguration object from File.
     * @param clazz Whether any class implementing IConfiguration with a constructor from File.
     * @param file The file you want to load.
     * @param <T> The class implementing IConfiguration you want to get.
     * @return A new instance of T implementing IConfiguration, null if an error occured.
     */
    @SuppressWarnings("unchecked")
    static <T extends IConfiguration> T loadConfiguration(Class<T> clazz, File file) {
        try {
            Constructor<?> constructor = ReflectionUtils.getConstructor(clazz, File.class);
            return (T) constructor.newInstance(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads in a IConfiguration object from InputStream.
     * @param clazz Whether any class implementing IConfiguration with a constructor from InputStream.
     * @param stream The InputStream you want to load.
     * @param <T> The class implementing IConfiguration you want to get.
     * @return A new instance of T implementing IConfiguration, null if an error occured.
     */
    @SuppressWarnings("unchecked")
    static <T extends IConfiguration> T loadConfiguration(Class<T> clazz, InputStream stream) {
        try {
            Constructor<?> constructor = ReflectionUtils.getConstructor(clazz, InputStream.class);
            return (T) constructor.newInstance(stream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads in a IConfiguration object from File.
     *
     * @param type JSON or YAML type.
     * @param file The file you want to load.
     * @return A new IConfiguration instance, null if an error occured.
     */
    @SuppressWarnings("unchecked")
    static IConfiguration loadConfiguration(FileStorageType type, File file) {
        try {
            Constructor<?> constructor = ReflectionUtils.getConstructor(getConfigurationClass(type), File.class);
            return (IConfiguration) constructor.newInstance(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads in a IConfiguration object from InputStream.
     *
     * @param type   JSON or YAML type.
     * @param stream The InputStream you want to load.
     * @return A new IConfiguration instance, null if an error occured.
     */
    @SuppressWarnings("unchecked")
    static IConfiguration loadConfiguration(FileStorageType type, InputStream stream) {
        try {
            Constructor<?> constructor = ReflectionUtils.getConstructor(getConfigurationClass(type), InputStream.class);
            return (IConfiguration) constructor.newInstance(stream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the default (built in) class for Json and Yaml file management.
     *
     * @param type JSON or YAML type.
     * @return The default BungeeUtilisals management for these classes.
     */
    static Class<?> getConfigurationClass(FileStorageType type) {
        switch (type) {
            default:
            case JSON:
                return ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.api.configuration.json.JsonConfiguration");
            case YAML:
                return ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.api.configuration.yaml.YamlConfiguration");
        }
    }

    /**
     * Copies keys and values from the given IConfiguration instance IF NOT found in the instance.
     * @param configuration The configuration you want to load defaults from.
     * @throws IOException If there is an error saving the file.
     */
    void copyDefaults(IConfiguration configuration) throws IOException;

    /**
     * Reloads the IConfiguration from File.
     * @throws IOException Being thrown if the File is not found. For example if you reload a IConfiguration built with a stream.
     */
    void reload() throws IOException;

    /**
     * Saves the IConfiguration to the File.
     * @throws IOException Being thrown if the File is not found. For example if you try to save a IConfiguration built with a stream.
     */
    void save() throws IOException;
}