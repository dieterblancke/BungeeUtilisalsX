package com.dbsoftwares.bungeeutilisals.api.json;

import com.dbsoftwares.bungeeutilisals.api.utils.ReflectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import lombok.Cleanup;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class JsonConfiguration implements IJsonConfiguration {

    private File file;
    private Object obj;
    private JsonObject object;
    Map<String, Object> values = Maps.newHashMap();

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

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            loadValues(entry.getKey(), entry.getValue());
        }

        stream.close();
        reader.close();
    }

    public JsonConfiguration(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input);
        Gson gson = new Gson();
        this.object = gson.fromJson(reader, JsonObject.class);
        this.obj = gson.fromJson(object, Object.class);
        loadValues(null, object);

        input.close();
        reader.close();
    }

    public JsonConfiguration(File file, JsonObject object) {
        this.file = file;
        this.object = object;
    }

    private void loadValues(String prefix, JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            Object value = getValue(primitive);

            if (value instanceof BigDecimal) {
                values.put(prefix, primitive.getAsBigDecimal());
            } else if (value instanceof BigInteger) {
                values.put(prefix, primitive.getAsBigInteger());
            } else if (primitive.isBoolean()) {
                values.put(prefix, primitive.getAsBoolean());
            } else if (primitive.isNumber()) {
                values.put(prefix, primitive.getAsNumber());
            } else if (primitive.isString()) {
                values.put(prefix, primitive.getAsString());
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            List<Object> list = Lists.newArrayList();

            for (JsonElement e : array) {
                if (!e.isJsonPrimitive()) {
                    continue;
                }
                JsonPrimitive primitive = e.getAsJsonPrimitive();
                Object value = getValue(primitive);
                if (value instanceof BigDecimal) {
                    list.add(primitive.getAsBigDecimal());
                } else if (value instanceof BigInteger) {
                    list.add(primitive.getAsBigInteger());
                } else if (primitive.isBoolean()) {
                    list.add(primitive.getAsBoolean());
                } else if (primitive.isNumber()) {
                    list.add(primitive.getAsNumber());
                } else if (primitive.isString()) {
                    list.add(primitive.getAsString());
                }
            }

            values.put(prefix, list);
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                System.out.println("Trying to load " + entry.getValue() + " at " + prefix);

                loadValues((prefix != null ? prefix + "." : "") + entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void copyDefaults(JsonConfiguration config) throws IOException {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        for (String key : config.getKeys()) {
            set(key, config.get(key));
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
        values.put(path, value);
        checkOrAdd(object, path, value);
    }

    @Override
    public Object get(String path) {
        return values.getOrDefault(path, null);
    }

    @Override
    public String getString(String path) {
        return (String) values.getOrDefault(path, null);
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
    public Integer getInteger(String path) {
        Number number = getNumber(path);
        return number == null ? null : number.intValue();
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
        return (Number) values.getOrDefault(path, null);
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
        Number number = getNumber(path);
        return number == null ? null : number.doubleValue();
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
        Number number = getNumber(path);
        return number == null ? null : number.longValue();
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
        Number number = getNumber(path);
        return number == null ? null : number.floatValue();
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
        Number number = getNumber(path);
        return number == null ? null : number.byteValue();
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
        Number number = getNumber(path);
        return number == null ? null : number.shortValue();
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
        return (BigInteger) values.getOrDefault(path, null);
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
        return (BigDecimal) values.getOrDefault(path, null);
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
        Iterator<String> it = Arrays.asList(path.split("\\.")).iterator();

        while (it.hasNext()) {
            String part = it.next();

            if (it.hasNext()) {
                if (!object.has(part)) {
                    object.add(part, new JsonObject());
                }
                object = object.getAsJsonObject(part);
            } else {
                if (object.has(part)) {
                    continue;
                }
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
    public Set<String> getKeys() {
        return values.keySet();
    }

    private Object getValue(JsonPrimitive primitive) {
        Field field = ReflectionUtils.getField(primitive.getClass(), "value");
        field.setAccessible(true);
        try {
            return field.get(primitive);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JsonObject getObject() {
        return object;
    }
}