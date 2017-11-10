package com.dbsoftwares.bungeeutilisals.api.json;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

public interface IJsonConfiguration {

    static JsonConfiguration loadConfiguration(File file) throws IOException {
        return new JsonConfiguration(file);
    }

    static JsonConfiguration loadConfiguration(InputStream input) throws IOException {
        return new JsonConfiguration(input);
    }

    void copyDefaults(JsonConfiguration configuration) throws IOException;

    void loadDefault(JsonConfiguration configuration) throws IOException;

    void set(String path, Object value);

    Object get(String key);

    String getString(String path);

    String getString(String path, String def);

    Character getCharacter(String path);

    Character getCharacter(String path, Character def);

    Integer getInteger(String path);

    Integer getInteger(String path, Integer def);

    Number getNumber(String path);

    Number getNumber(String path, Number def);


    Double getDouble(String path);

    Double getDouble(String path, Double def);

    Long getLong(String path);

    Long getLong(String path, Long def);

    Float getFloat(String path);

    Float getFloat(String path, Float def);

    Byte getByte(String path);

    Byte getByte(String path, Byte def);

    Short getShort(String path);

    Short getShort(String path, Short def);

    BigInteger getBigInteger(String path);

    BigInteger getBigInteger(String path, BigInteger def);

    BigDecimal getBigDecimal(String path);

    BigDecimal getBigDecimal(String path, BigDecimal def);

    void reload() throws IOException;

    void save() throws IOException;

    JsonConfiguration getSection(String section);

    Set<String> getKeys(Boolean deep);
}