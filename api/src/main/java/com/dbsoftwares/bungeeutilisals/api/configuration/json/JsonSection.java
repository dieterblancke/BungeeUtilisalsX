package com.dbsoftwares.bungeeutilisals.api.configuration.json;

import com.dbsoftwares.bungeeutilisals.api.configuration.ISection;
import com.dbsoftwares.bungeeutilisals.api.utils.ReflectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonSection implements ISection {

    String prefix;
    ISection parent;
    JsonObject object;
    LinkedHashMap<String, Object> values = Maps.newLinkedHashMap();

    public JsonSection(String prefix, JsonConfiguration parent) {
        this.prefix = prefix;
        this.parent = parent;
        this.object = parent.getJsonObject(prefix);

        loadValues(null, object);
    }

    public JsonSection(String prefix, JsonSection parent) {
        this.prefix = prefix;
        this.parent = parent;
        this.object = parent.getJsonObject(prefix);

        loadValues(null, object);
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
                loadValues((prefix != null ? prefix + "." : "") + entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Boolean exists(String path) {
        return values.containsKey(path);
    }

    @Override
    public void set(String path, Object value) {
        parent.set(getPath(path), value);
    }

    @Override
    public <T> T get(String path, T def) {
        return parent.get(path, def);
    }

    @Override
    public <T> T get(String path) {
        return get(path, null);
    }

    @Override
    public Boolean isString(String path) {
        return parent.isString(getPath(path));
    }

    @Override
    public String getString(String path) {
        return parent.getString(getPath(path));
    }

    @Override
    public String getString(String path, String def) {
        return parent.getString(getPath(path), def);
    }

    @Override
    public Boolean isBoolean(String path) {
        return parent.isBoolean(getPath(path));
    }

    @Override
    public Boolean getBoolean(String path) {
        return parent.getBoolean(getPath(path));
    }

    @Override
    public Boolean getBoolean(String path, Boolean def) {
        return parent.getBoolean(getPath(path), def);
    }

    @Override
    public Boolean isInteger(String path) {
        return parent.isInteger(getPath(path));
    }

    @Override
    public Integer getInteger(String path) {
        return parent.getInteger(getPath(path));
    }

    @Override
    public Integer getInteger(String path, Integer def) {
        return parent.getInteger(getPath(path), def);
    }

    @Override
    public Boolean isNumber(String path) {
        return parent.isNumber(getPath(path));
    }

    @Override
    public Number getNumber(String path) {
        return parent.getNumber(getPath(path));
    }

    @Override
    public Number getNumber(String path, Number def) {
        return parent.getNumber(getPath(path), def);
    }

    @Override
    public Boolean isDouble(String path) {
        return parent.isDouble(getPath(path));
    }

    @Override
    public Double getDouble(String path) {
        return parent.getDouble(getPath(path));
    }

    @Override
    public Double getDouble(String path, Double def) {
        return parent.getDouble(getPath(path), def);
    }

    @Override
    public Boolean isLong(String path) {
        return parent.isLong(getPath(path));
    }

    @Override
    public Long getLong(String path) {
        return parent.getLong(getPath(path));
    }

    @Override
    public Long getLong(String path, Long def) {
        return parent.getLong(getPath(path), def);
    }

    @Override
    public Boolean isFloat(String path) {
        return parent.isFloat(getPath(path));
    }

    @Override
    public Float getFloat(String path) {
        return parent.getFloat(getPath(path));
    }

    @Override
    public Float getFloat(String path, Float def) {
        return parent.getFloat(getPath(path), def);
    }

    @Override
    public Boolean isByte(String path) {
        return parent.isByte(getPath(path));
    }

    @Override
    public Byte getByte(String path) {
        return parent.getByte(getPath(path));
    }

    @Override
    public Byte getByte(String path, Byte def) {
        return parent.getByte(getPath(path), def);
    }

    @Override
    public Boolean isShort(String path) {
        return parent.isShort(getPath(path));
    }

    @Override
    public Short getShort(String path) {
        return parent.getShort(getPath(path));
    }

    @Override
    public Short getShort(String path, Short def) {
        return parent.getShort(getPath(path), def);
    }

    @Override
    public Boolean isBigInteger(String path) {
        return parent.isBigInteger(getPath(path));
    }

    @Override
    public BigInteger getBigInteger(String path) {
        return parent.getBigInteger(getPath(path));
    }

    @Override
    public BigInteger getBigInteger(String path, BigInteger def) {
        return parent.getBigInteger(getPath(path), def);
    }

    @Override
    public Boolean isBigDecimal(String path) {
        return parent.isBigInteger(getPath(path));
    }

    @Override
    public BigDecimal getBigDecimal(String path) {
        return parent.getBigDecimal(getPath(path));
    }

    @Override
    public BigDecimal getBigDecimal(String path, BigDecimal def) {
        return parent.getBigDecimal(getPath(path), def);
    }

    @Override
    public Boolean isList(String path) {
        return parent.isList(getPath(path));
    }

    @Override
    public List getList(String path) {
        return parent.getList(getPath(path));
    }

    @Override
    public List getList(String path, List def) {
        return parent.getList(getPath(path), def);
    }

    @Override
    public List<String> getStringList(String path) {
        return parent.getStringList(getPath(path));
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        return parent.getStringList(getPath(path), def);
    }

    @Override
    public List<Integer> getIntegerList(String path) {
        return parent.getIntegerList(getPath(path));
    }

    @Override
    public List<Integer> getIntegerList(String path, List<Integer> def) {
        return parent.getIntegerList(getPath(path), def);
    }

    @Override
    public List<Double> getDoubleList(String path) {
        return parent.getDoubleList(getPath(path));
    }

    @Override
    public List<Double> getDoubleList(String path, List<Double> def) {
        return parent.getDoubleList(getPath(path), def);
    }

    @Override
    public List<Boolean> getBooleanList(String path) {
        return parent.getBooleanList(getPath(path));
    }

    @Override
    public List<Boolean> getBooleanList(String path, List<Boolean> def) {
        return parent.getBooleanList(getPath(path), def);
    }

    @Override
    public List<Long> getLongList(String path) {
        return parent.getLongList(getPath(path));
    }

    @Override
    public List<Long> getLongList(String path, List<Long> def) {
        return parent.getLongList(getPath(path), def);
    }

    @Override
    public List<Byte> getByteList(String path) {
        return parent.getByteList(getPath(path));
    }

    @Override
    public List<Byte> getByteList(String path, List<Byte> def) {
        return parent.getByteList(getPath(path), def);
    }

    @Override
    public List<Short> getShortList(String path) {
        return parent.getShortList(getPath(path));
    }

    @Override
    public List<Short> getShortList(String path, List<Short> def) {
        return parent.getShortList(getPath(path), def);
    }

    @Override
    public List<Float> getFloatList(String path) {
        return parent.getFloatList(getPath(path));
    }

    @Override
    public List<Float> getFloatList(String path, List<Float> def) {
        return parent.getFloatList(getPath(path), def);
    }

    @Override
    public List<Number> getNumberList(String path) {
        return parent.getNumberList(getPath(path));
    }

    @Override
    public List<Number> getNumberList(String path, List<Number> def) {
        return parent.getNumberList(getPath(path), def);
    }

    @Override
    public List<BigInteger> getBigIntegerList(String path) {
        return parent.getBigIntegerList(getPath(path));
    }

    @Override
    public List<BigInteger> getBigIntegerList(String path, List<BigInteger> def) {
        return parent.getBigIntegerList(getPath(path), def);
    }

    @Override
    public List<BigDecimal> getBigDecimalList(String path) {
        return parent.getBigDecimalList(getPath(path));
    }

    @Override
    public List<BigDecimal> getBigDecimalList(String path, List<BigDecimal> def) {
        return parent.getBigDecimalList(getPath(path), def);
    }

    @Override
    public ISection getSection(String section) {
        return parent.getSection(getPath(section));
    }

    @Override
    public void createSection(String section) {
        parent.createSection(getPath(section));
    }

    @Override
    public Set<String> getKeys() {
        return values.keySet();
    }

    @Override
    public Set<String> getKeys(String path) {
        Set<String> keys = Sets.newHashSet();

        for (String key : getKeys()) {
            if (key.startsWith(path)) {
                keys.add(key);
            }
        }
        return keys;
    }

    public JsonObject getJsonObject(String path) {
        JsonObject object = this.object;
        for (String part : path.split("\\.")) {
            if (!object.has(part) || !object.get(part).isJsonObject()) {
                return null;
            }
            object = object.getAsJsonObject(part);
        }
        return object;
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

    private String getPath(String path) {
        return prefix + "." + path;
    }
}