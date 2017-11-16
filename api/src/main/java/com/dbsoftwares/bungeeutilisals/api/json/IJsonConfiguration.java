package com.dbsoftwares.bungeeutilisals.api.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface IJsonConfiguration extends JsonSection {

    /**
     * Loads in a JsonConfiguration object from file.
     * @param file The file that has to be read.
     * @return A new JsonConfiguration instance for the given file.
     * @throws IOException If there is an error initializing or closing the stream.
     */
    static JsonConfiguration loadConfiguration(File file) throws IOException {
        return new JsonConfiguration(file);
    }

    /**
     * Loads in a JsonConfiguration object from InputStream.
     * @param input The stream that has to be read.
     * @return A new JsonConfiguration instance for the given stream.
     * @throws IOException If there is an error closing the stream.
     */
    static JsonConfiguration loadConfiguration(InputStream input) throws IOException {
        return new JsonConfiguration(input);
    }

    /**
     * Copies keys & values from the given JsonConfiguration instance IF NOT found in the instance.
     * @param configuration The configuration you want to load defaults from.
     * @throws IOException If there is an error saving the file.
     */
    void copyDefaults(JsonConfiguration configuration) throws IOException;

    /**
     * Fully copy-paste of the given JsonConfiguration instance. This will overwrite existing values.
     * @param configuration The configuration you want to copy-paste.
     * @throws IOException If there is an error saving the file.
     */
    void loadDefault(JsonConfiguration configuration) throws IOException;

    /**
     * Reloads the JsonConfiguration from File.
     * @throws IOException Being thrown if the File is not found. For example if you reload a JsonConfiguration built with a stream.
     */
    void reload() throws IOException;

    /**
     * Saves the JsonConfiguration to the File.
     * @throws IOException Being thrown if the File is not found. For example if you try to save a JsonConfiguration built with a stream.
     */
    void save() throws IOException;
}