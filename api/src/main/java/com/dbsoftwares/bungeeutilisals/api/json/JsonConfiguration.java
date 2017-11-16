package com.dbsoftwares.bungeeutilisals.api.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.*;
import lombok.Cleanup;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class JsonConfiguration implements IJsonConfiguration {

    private File file;
    private Object obj;
    private JsonObject object;
    private InputStream stream;

    public JsonConfiguration(File file) throws IOException {
        this.file = file;
        if (!file.exists()) {
            obj = new Object();
            object = new JsonObject();
            return;
        }
        FileInputStream stream = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(stream);

        Gson gson = new Gson();
        this.object = gson.fromJson(reader, JsonObject.class);
        this.obj = gson.fromJson(object, Object.class);

        stream.close();
        reader.close();
    }

    public JsonConfiguration(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input);
        Gson gson = new Gson();
        this.stream = input;
        this.object = gson.fromJson(reader, JsonObject.class);
        this.obj = gson.fromJson(object, Object.class);
        reader.close();
    }

    public JsonConfiguration(File file, JsonObject object) {
        this.file = file;
        this.object = object;
    }

    @Override
    public void copyDefaults(JsonConfiguration config) throws IOException {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        for (String key : config.getKeys(true)) {
            Object object = config.get(key);

            // System.out.println("Setting key " + key + " to " + object);
            if (object == null) {
                continue;
            }
            set(key, object);
        }
        save();
    }

    @Override
    public void loadDefault(JsonConfiguration config) throws IOException {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        this.object = config.getObject();

        save();
    }

    @Override
    public void set(String path, Object value) {
        checkOrAdd(object, path, value);
    }

    @Override
    public Object get(String path) {
        JsonObject object = this.object;
        Iterator<String> it = Lists.newArrayList(path.split("\\.")).iterator();
        String lastpart = null;

        while (it.hasNext()) {
            String part = it.next();

            if (it.hasNext()) {
                if (!object.has(part)) {
                    System.out.println("Did not find " + part + " in " + lastpart);
                    continue;
                }
                object = object.getAsJsonObject(part);
                System.out.println("Getting " + part + " child of " + (lastpart == null ? "super part" : lastpart));
            } else {
                JsonElement element = object.get(part);
                System.out.println("Getting element in " + lastpart + ": " + element);

                if (element.isJsonObject()) {
                    return element.getAsJsonObject();
                } else if (element.isJsonArray()) {
                    return element.getAsJsonArray();
                } else if (element.isJsonPrimitive()) {
                    JsonPrimitive primitive = element.getAsJsonPrimitive();

                    if (primitive.isBoolean()) {
                        return primitive.getAsBoolean();
                    } else if (primitive.isNumber()) {
                        return primitive.getAsNumber();
                    } else if (primitive.isString()) {
                        return primitive.getAsString();
                    }
                } else {
                    return element.getAsJsonNull();
                }
            }
            lastpart = part;
        }
        return null;
    }

    @Override
    public String getString(String path) {
        for (String part : path.split("\\.")) {
            if (!object.has(part)) {
                continue;
            }
            if (object.get(part).isJsonObject()) {
                object = object.getAsJsonObject(part);
            } else if (object.get(part).isJsonPrimitive()) {
                return object.get(part).getAsString();
            }
        }
        return null;
    }

    @Override
    public String getString(String path, String def) {
        String result = getString(path);

        if (result == null) {
            checkOrAdd(object, path, def);
        }
        return result == null ? def : result;
    }

    @Override
    public Character getCharacter(String path) {
        for (String part : path.split("\\.")) {
            if (!object.has(part)) {
                continue;
            }
            if (object.get(part).isJsonObject()) {
                object = object.getAsJsonObject(part);
            } else if (object.get(part).isJsonPrimitive()) {
                return object.get(part).getAsCharacter();
            }
        }
        return null;
    }

    @Override
    public Character getCharacter(String path, Character def) {
        Character result = getCharacter(path);

        if (result == null) {
            checkOrAdd(object, path, def);
        }
        return result == null ? def : result;
    }

    @Override
    public Integer getInteger(String path) {
        for (String part : path.split("\\.")) {
            if (!object.has(part)) {
                continue;
            }
            if (object.get(part).isJsonObject()) {
                object = object.getAsJsonObject(part);
            } else if (object.get(part).isJsonPrimitive()) {
                return object.get(part).getAsInt();
            }
        }
        return null;
    }

    @Override
    public Integer getInteger(String path, Integer def) {
        Integer result = getInteger(path);

        if (result == null) {
            checkOrAdd(object, path, def);
        }
        return result == null ? def : result;
    }

    @Override
    public Number getNumber(String path) {
        for (String part : path.split("\\.")) {
            if (!object.has(part)) {
                continue;
            }
            if (object.get(part).isJsonObject()) {
                object = object.getAsJsonObject(part);
            } else if (object.get(part).isJsonPrimitive()) {
                return object.get(part).getAsNumber();
            }
        }
        return null;
    }

    @Override
    public Number getNumber(String path, Number def) {
        Number result = getNumber(path);

        if (result == null) {
            checkOrAdd(object, path, def);
        }
        return result == null ? def : result;
    }


    @Override
    public Double getDouble(String path) {
        for (String part : path.split("\\.")) {
            if (!object.has(part)) {
                continue;
            }
            if (object.get(part).isJsonObject()) {
                object = object.getAsJsonObject(part);
            } else if (object.get(part).isJsonPrimitive()) {
                return object.get(part).getAsDouble();
            }
        }
        return null;
    }

    @Override
    public Double getDouble(String path, Double def) {
        Double result = getDouble(path);

        if (result == null) {
            checkOrAdd(object, path, def);
        }
        return result == null ? def : result;
    }

    @Override
    public Long getLong(String path) {
        for (String part : path.split("\\.")) {
            if (!object.has(part)) {
                continue;
            }
            if (object.get(part).isJsonObject()) {
                object = object.getAsJsonObject(part);
            } else if (object.get(part).isJsonPrimitive()) {
                return object.get(part).getAsLong();
            }
        }
        return null;
    }

    @Override
    public Long getLong(String path, Long def) {
        Long result = getLong(path);

        if (result == null) {
            checkOrAdd(object, path, def);
        }
        return result == null ? def : result;
    }

    @Override
    public Float getFloat(String path) {
        for (String part : path.split("\\.")) {
            if (!object.has(part)) {
                continue;
            }
            if (object.get(part).isJsonObject()) {
                object = object.getAsJsonObject(part);
            } else if (object.get(part).isJsonPrimitive()) {
                return object.get(part).getAsFloat();
            }
        }
        return null;
    }

    @Override
    public Float getFloat(String path, Float def) {
        Float result = getFloat(path);

        if (result == null) {
            checkOrAdd(object, path, def);
        }
        return result == null ? def : result;
    }

    @Override
    public Byte getByte(String path) {
        for (String part : path.split("\\.")) {
            if (!object.has(part)) {
                continue;
            }
            if (object.get(part).isJsonObject()) {
                object = object.getAsJsonObject(part);
            } else if (object.get(part).isJsonPrimitive()) {
                return object.get(part).getAsByte();
            }
        }
        return null;
    }

    @Override
    public Byte getByte(String path, Byte def) {
        Byte result = getByte(path);

        if (result == null) {
            checkOrAdd(object, path, def);
        }
        return result == null ? def : result;
    }

    @Override
    public Short getShort(String path) {
        for (String part : path.split("\\.")) {
            if (!object.has(part)) {
                continue;
            }
            if (object.get(part).isJsonObject()) {
                object = object.getAsJsonObject(part);
            } else if (object.get(part).isJsonPrimitive()) {
                return object.get(part).getAsShort();
            }
        }
        return null;
    }

    @Override
    public Short getShort(String path, Short def) {
        Short result = getShort(path);

        if (result == null) {
            checkOrAdd(object, path, def);
        }
        return result == null ? def : result;
    }

    @Override
    public BigInteger getBigInteger(String path) {
        for (String part : path.split("\\.")) {
            if (!object.has(part)) {
                continue;
            }
            if (object.get(part).isJsonObject()) {
                object = object.getAsJsonObject(part);
            } else if (object.get(part).isJsonPrimitive()) {
                return object.get(part).getAsBigInteger();
            }
        }
        return null;
    }

    @Override
    public BigInteger getBigInteger(String path, BigInteger def) {
        BigInteger result = getBigInteger(path);

        if (result == null) {
            checkOrAdd(object, path, def);
        }
        return result == null ? def : result;
    }

    @Override
    public BigDecimal getBigDecimal(String path) {
        for (String part : path.split("\\.")) {
            if (!object.has(part)) {
                continue;
            }
            if (object.get(part).isJsonObject()) {
                object = object.getAsJsonObject(part);
            } else if (object.get(part).isJsonPrimitive()) {
                return object.get(part).getAsBigDecimal();
            }
        }
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String path, BigDecimal def) {
        BigDecimal result = getBigDecimal(path);

        if (result == null) {
            checkOrAdd(object, path, def);
        }
        return result == null ? def : result;
    }

    @Override
    public void reload() throws IOException {
        @Cleanup FileInputStream stream = new FileInputStream(file);
        @Cleanup InputStreamReader reader = new InputStreamReader(stream);

        Gson gson = new Gson();
        this.object = gson.fromJson(reader, JsonObject.class);
    }

    @Override
    public void save() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        @Cleanup FileWriter fileWriter = new FileWriter(file);
        @Cleanup BufferedWriter writer = new BufferedWriter(fileWriter);

        writer.write(gson.toJson(object));
        writer.flush();
    }

    private void checkOrAdd(JsonObject object, String path, Object def) {
        Iterator<String> it = Lists.newArrayList(path.split("\\.")).iterator();

        while (it.hasNext()) {
            String part = it.next();

            if (it.hasNext()) {
                if (!object.has(part)) {
                    object.add(part, new JsonObject());
                }
                if (object.get(part).isJsonObject()) {
                    object = object.getAsJsonObject(part);
                }
            } else {
                if (def instanceof Character) {
                    object.addProperty(part, (Character) def);
                } else if (def instanceof Number) {
                    object.addProperty(part, (Number) def);
                } else if (def instanceof String) {
                    object.addProperty(part, (String) def);
                } else if (def instanceof Boolean) {
                    object.addProperty(part, (Boolean) def);
                } else {
                    // Attempt String storage in case of other type.
                    object.addProperty(part, def.toString());
                }
            }
        }
    }

    @Override
    public JsonConfiguration getSection(String section) {
        JsonObject object = this.object;

        if (section.isEmpty()) {
            return new JsonConfiguration(file, object);
        }

        for (String part : section.split(".")) {
            if (!object.has(part) || !object.get(part).isJsonObject()) {
                continue;
            }
            object = object.getAsJsonObject(part);
        }

        return new JsonConfiguration(file, object);
    }

    @Override
    public Set<String> getKeys(Boolean deep) {
        if (deep) {
            return collectAllKeys(obj, "");
        } else {
            Set<String> set = Sets.newHashSet();
            object.entrySet().forEach(entry -> {
                if (entry.getValue().isJsonObject()) {
                    set.add(entry.getKey());
                }
            });
            return set;
        }
    }

    @SuppressWarnings("unchecked")
    private Set<String> collectAllKeys(Object object, String path) {
        Set<String> keys = Sets.newHashSet();

        if (object instanceof Map) {
            Map map = (Map) object;

            for (Object key : map.keySet()) {
                keys.add(path.isEmpty() ? (String) key : path + "." + key);
            }

            for (Object ent : map.entrySet()) {
                Map.Entry entry = (Map.Entry) ent;

                keys.addAll(collectAllKeys(entry.getValue(), path.isEmpty() ? (String) entry.getKey() : path + "." + entry.getKey()));
            }
        } else if (object instanceof Collection) {
            for (Object o : (Collection) object) {
                keys.addAll(collectAllKeys(o, path));
            }
        } else {
            return keys;
        }

        return keys;
    }

    private JsonObject getObject() {
        return object;
    }

    private InputStream getStream() {
        return stream;
    }
}