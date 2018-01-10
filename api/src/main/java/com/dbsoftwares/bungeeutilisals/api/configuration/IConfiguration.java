package com.dbsoftwares.bungeeutilisals.api.configuration;

import com.dbsoftwares.bungeeutilisals.api.configuration.json.JsonConfiguration;
import com.dbsoftwares.bungeeutilisals.api.configuration.yaml.YamlConfiguration;
import com.dbsoftwares.bungeeutilisals.api.utils.ReflectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

public interface IConfiguration extends ISection {

    /**
     * Loads in a IConfiguration object from file.
     * @param file The file that has to be read.
     * @return A new IConfiguration instance for the given file.
     * @throws IOException If there is an error initializing or closing the stream.
     */
    static JsonConfiguration loadConfiguration(File file) throws IOException {
        return new JsonConfiguration(file);
    }

    /**
     * Loads in a IConfiguration object from InputStream.
     * @param input The stream that has to be read.
     * @return A new IConfiguration instance for the given stream.
     * @throws IOException If there is an error closing the stream.
     */
    static JsonConfiguration loadConfiguration(InputStream input) throws IOException {
        return new JsonConfiguration(input);
    }

    /**
     * Loads in a IConfiguration object from file.
     * @param file The file that has to be read.
     * @return A new IConfiguration instance for the given file.
     * @throws IOException If there is an error initializing or closing the stream.
     */
    static JsonConfiguration loadJsonConfiguration(File file) throws IOException {
        return new JsonConfiguration(file);
    }

    /**
     * Loads in a IConfiguration object from InputStream.
     * @param input The stream that has to be read.
     * @return A new IConfiguration instance for the given stream.
     * @throws IOException If there is an error closing the stream.
     */
    static JsonConfiguration loadJsonConfiguration(InputStream input) throws IOException {
        return new JsonConfiguration(input);
    }

    /**
     * Loads in a IConfiguration object from file.
     * @param file The file that has to be read.
     * @return A new IConfiguration instance for the given file.
     * @throws IOException If there is an error initializing or closing the stream.
     */
    static YamlConfiguration loadYamlConfiguration(File file) throws IOException {
        return new YamlConfiguration(file);
    }

    /**
     * Loads in a IConfiguration object from InputStream.
     * @param input The stream that has to be read.
     * @return A new IConfiguration instance for the given stream.
     * @throws IOException If there is an error closing the stream.
     */
    static YamlConfiguration loadYamlConfiguration(InputStream input) throws IOException {
        return new YamlConfiguration(input);
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