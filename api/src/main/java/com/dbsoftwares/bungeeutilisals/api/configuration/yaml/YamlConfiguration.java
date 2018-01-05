package com.dbsoftwares.bungeeutilisals.api.configuration.yaml;

import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.configuration.ISection;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.math.MathUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Cleanup;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class YamlConfiguration implements IConfiguration {

    private final ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>() {
        protected Yaml initialValue() {
            Representer representer = new Representer() {
                {
                    this.representers.put(YamlConfiguration.class, data -> represent(values));
                }
            };
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setAllowUnicode(true);
            return new Yaml(new Constructor(), representer, options);
        }
    };
    private File file;
    LinkedHashMap<String, Object> values = Maps.newLinkedHashMap();

    public YamlConfiguration(File file) throws IOException {
        this(new FileInputStream(file));
        this.file = file;
    }

    @SuppressWarnings("unchecked")
    public YamlConfiguration(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input);
        Gson gson = new Gson();

        LinkedHashMap<String, Object> values = (LinkedHashMap<String, Object>) yaml.get().loadAs(reader, LinkedHashMap.class);
        if (values == null) {
            values = new LinkedHashMap<>();
        }

        this.values = getValuesDeep("", values);

        input.close();
        reader.close();
    }

    @SuppressWarnings("unchecked")
    private LinkedHashMap<String, Object> getValuesDeep(String prefix, Map<String, Object> map) {
        LinkedHashMap<String, Object> values = Maps.newLinkedHashMap();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                values.putAll(getValuesDeep((prefix.isEmpty() ? "" : prefix + ".") + entry.getKey(),
                        (Map<String, Object>) entry.getValue()));
            } else {
                values.put((prefix.isEmpty() ? "" : prefix + ".") + entry.getKey(), entry.getValue());
            }
        }

        return values;
    }

    @Override
    public void copyDefaults(IConfiguration config) throws IOException {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        for (String key : config.getKeys()) {
            if (!exists(key)) {
                set(key, config.get(key));
            }
        }
        save();
    }

    @Override
    public Boolean exists(String path) {
        return values.containsKey(path);
    }

    @Override
    public void set(String path, Object value) {
        values.put(path, value);

        update(path, value, true);
    }

    @Override
    public Object get(String path) {
        return values.getOrDefault(path, null);
    }

    @Override
    public Boolean isString(String path) {
        Object object = get(path);
        return object != null && object instanceof String;
    }

    @Override
    public String getString(String path) {
        return (String) values.getOrDefault(path, null);
    }

    @Override
    public String getString(String path, String def) {
        String result = getString(path);

        if (result == null) {
            update(path, def, false);
        }
        return result == null ? def : result;
    }

    @Override
    public Boolean isBoolean(String path) {
        Object object = get(path);
        return object != null && object instanceof Boolean;
    }

    @Override
    public Boolean getBoolean(String path) {
        return (Boolean) values.getOrDefault(path, null);
    }

    @Override
    public Boolean getBoolean(String path, Boolean def) {
        Boolean result = getBoolean(path);

        if (result == null) {
            update(path, def, false);
        }
        return result == null ? def : result;
    }

    @Override
    public Boolean isInteger(String path) {
        return isNumber(path);
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
            update(path, def, false);
        }
        return result == null ? def : result;
    }

    @Override
    public Boolean isNumber(String path) {
        Object object = get(path);
        return object != null && object instanceof Number;
    }

    @Override
    public Number getNumber(String path) {
        return (Number) values.getOrDefault(path, null);
    }

    @Override
    public Number getNumber(String path, Number def) {
        Number result = getNumber(path);

        if (result == null) {
            update(path, def, false);
        }
        return result == null ? def : result;
    }

    @Override
    public Boolean isDouble(String path) {
        return isNumber(path);
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
            update(path, def, false);
        }
        return result == null ? def : result;
    }

    @Override
    public Boolean isLong(String path) {
        return isNumber(path);
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
            update(path, def, false);
        }
        return result == null ? def : result;
    }

    @Override
    public Boolean isFloat(String path) {
        return isNumber(path);
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
            update(path, def, false);
        }
        return result == null ? def : result;
    }

    @Override
    public Boolean isByte(String path) {
        return isNumber(path);
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
            update(path, def, false);
        }
        return result == null ? def : result;
    }

    @Override
    public Boolean isShort(String path) {
        return isNumber(path);
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
            update(path, def, false);
        }
        return result == null ? def : result;
    }

    @Override
    public Boolean isBigInteger(String path) {
        Object object = get(path);
        return object != null && object instanceof BigInteger;
    }

    @Override
    public BigInteger getBigInteger(String path) {
        return (BigInteger) values.getOrDefault(path, null);
    }

    @Override
    public BigInteger getBigInteger(String path, BigInteger def) {
        BigInteger result = getBigInteger(path);

        if (result == null) {
            update(path, def, false);
        }
        return result == null ? def : result;
    }

    @Override
    public Boolean isBigDecimal(String path) {
        Object object = get(path);
        return object != null && object instanceof BigDecimal;
    }

    @Override
    public BigDecimal getBigDecimal(String path) {
        return (BigDecimal) values.getOrDefault(path, null);
    }

    @Override
    public BigDecimal getBigDecimal(String path, BigDecimal def) {
        BigDecimal result = getBigDecimal(path);

        if (result == null) {
            update(path, def, false);
        }
        return result == null ? def : result;
    }

    @Override
    public Boolean isList(String path) {
        Object object = get(path);
        return object != null && object instanceof List;
    }

    @Override
    public List getList(String path) {
        return (List) values.getOrDefault(path, null);
    }

    @Override
    public List getList(String path, List def) {
        List result = getList(path);

        if (result == null) {
            update(path, def, false);
        }

        return result == null ? def : result;
    }

    @Override
    public List<String> getStringList(String path) {
        List list = getList(path);

        if (list == null) {
            return null;
        }
        List<String> strList = Lists.newArrayList();
        for (Object object : list) {
            strList.add(object.toString());
        }
        return strList;
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        List<String> result = getStringList(path);

        if (result == null) {
            update(path, def, false);
        }

        return result == null ? def : result;
    }

    @Override
    public List<Integer> getIntegerList(String path) {
        List list = getList(path);

        if (list == null) {
            return null;
        }
        List<Integer> intList = Lists.newArrayList();
        for (Object object : list) {
            if (object instanceof Number) {
                intList.add(((Number) object).intValue());
            } else if (MathUtils.isInteger(object)) {
                intList.add(Integer.parseInt(object.toString()));
            }
        }
        return intList;
    }

    @Override
    public List<Integer> getIntegerList(String path, List<Integer> def) {
        List<Integer> result = getIntegerList(path);

        if (result == null) {
            update(path, def, false);
        }

        return result == null ? def : result;
    }

    @Override
    public List<Double> getDoubleList(String path) {
        List list = getList(path);

        if (list == null) {
            return null;
        }
        List<Double> dList = Lists.newArrayList();
        for (Object object : list) {
            if (object instanceof Number) {
                dList.add(((Number) object).doubleValue());
            } else if (MathUtils.isDouble(object)) {
                dList.add(Double.parseDouble(object.toString()));
            }
        }
        return dList;
    }

    @Override
    public List<Double> getDoubleList(String path, List<Double> def) {
        List<Double> result = getDoubleList(path);

        if (result == null) {
            update(path, def, false);
        }

        return result == null ? def : result;
    }

    @Override
    public List<Boolean> getBooleanList(String path) {
        List list = getList(path);

        if (list == null) {
            return null;
        }
        List<Boolean> bList = Lists.newArrayList();
        for (Object object : list) {
            if (object instanceof Boolean) {
                bList.add((Boolean) object);
            } else if (Utils.isBoolean(object)) {
                bList.add(Boolean.parseBoolean(object.toString()));
            }
        }
        return bList;
    }

    @Override
    public List<Boolean> getBooleanList(String path, List<Boolean> def) {
        List<Boolean> result = getBooleanList(path);

        if (result == null) {
            update(path, def, false);
        }

        return result == null ? def : result;
    }

    @Override
    public List<Long> getLongList(String path) {
        List list = getList(path);

        if (list == null) {
            return null;
        }
        List<Long> longList = Lists.newArrayList();
        for (Object object : list) {
            if (object instanceof Number) {
                longList.add(((Number) object).longValue());
            } else if (MathUtils.isLong(object)) {
                longList.add(Long.parseLong(object.toString()));
            }
        }
        return longList;
    }

    @Override
    public List<Long> getLongList(String path, List<Long> def) {
        List<Long> result = getLongList(path);

        if (result == null) {
            update(path, def, false);
        }

        return result == null ? def : result;
    }

    @Override
    public List<Byte> getByteList(String path) {
        List list = getList(path);

        if (list == null) {
            return null;
        }
        List<Byte> bList = Lists.newArrayList();
        for (Object object : list) {
            if (object instanceof Number) {
                bList.add(((Number) object).byteValue());
            } else if (MathUtils.isByte(object)) {
                bList.add(Byte.parseByte(object.toString()));
            }
        }
        return bList;
    }

    @Override
    public List<Byte> getByteList(String path, List<Byte> def) {
        List<Byte> result = getByteList(path);

        if (result == null) {
            update(path, def, false);
        }

        return result == null ? def : result;
    }

    @Override
    public List<Short> getShortList(String path) {
        List list = getList(path);

        if (list == null) {
            return null;
        }
        List<Short> shortList = Lists.newArrayList();
        for (Object object : list) {
            if (object instanceof Number) {
                shortList.add(((Number) object).shortValue());
            } else if (MathUtils.isShort(object)) {
                shortList.add(Short.parseShort(object.toString()));
            }
        }
        return shortList;
    }

    @Override
    public List<Short> getShortList(String path, List<Short> def) {
        List<Short> result = getShortList(path);

        if (result == null) {
            update(path, def, false);
        }

        return result == null ? def : result;
    }

    @Override
    public List<Float> getFloatList(String path) {
        List list = getList(path);

        if (list == null) {
            return null;
        }
        List<Float> floatList = Lists.newArrayList();
        for (Object object : list) {
            if (object instanceof Number) {
                floatList.add(((Number) object).floatValue());
            } else if (MathUtils.isFloat(object)) {
                floatList.add(Float.parseFloat(object.toString()));
            }
        }
        return floatList;
    }

    @Override
    public List<Float> getFloatList(String path, List<Float> def) {
        List<Float> result = getFloatList(path);

        if (result == null) {
            update(path, def, false);
        }

        return result == null ? def : result;
    }

    @Override
    public List<Number> getNumberList(String path) {
        List list = getList(path);

        if (list == null) {
            return null;
        }
        List<Number> numberList = Lists.newArrayList();
        for (Object object : list) {
            if (object instanceof Number) {
                numberList.add((Number) object);
            } else if (MathUtils.isInteger(object)) {
                numberList.add(Integer.parseInt(object.toString()));
            } else if (MathUtils.isShort(object)) {
                numberList.add(Short.parseShort(object.toString()));
            } else if (MathUtils.isByte(object)) {
                numberList.add(Byte.parseByte(object.toString()));
            } else if (MathUtils.isDouble(object)) {
                numberList.add(Double.parseDouble(object.toString()));
            } else if (MathUtils.isLong(object)) {
                numberList.add(Long.parseLong(object.toString()));
            } else if (MathUtils.isFloat(object)) {
                numberList.add(Float.parseFloat(object.toString()));
            }
        }
        return numberList;
    }

    @Override
    public List<Number> getNumberList(String path, List<Number> def) {
        List<Number> result = getNumberList(path);

        if (result == null) {
            update(path, def, false);
        }

        return result == null ? def : result;
    }

    @Override
    public List<BigInteger> getBigIntegerList(String path) {
        List list = getList(path);

        if (list == null) {
            return null;
        }
        List<BigInteger> biList = Lists.newArrayList();
        for (Object object : list) {
            if (MathUtils.isBigInteger(object.toString())) {
                biList.add(new BigInteger(object.toString()));
            }
        }
        return biList;
    }

    @Override
    public List<BigInteger> getBigIntegerList(String path, List<BigInteger> def) {
        List<BigInteger> result = getBigIntegerList(path);

        if (result == null) {
            update(path, def, false);
        }

        return result == null ? def : result;
    }

    @Override
    public List<BigDecimal> getBigDecimalList(String path) {
        List list = getList(path);

        if (list == null) {
            return null;
        }
        List<BigDecimal> bdList = Lists.newArrayList();
        for (Object object : list) {
            if (MathUtils.isBigDecimal(object.toString())) {
                bdList.add(new BigDecimal(object.toString()));
            }
        }
        return bdList;
    }

    @Override
    public List<BigDecimal> getBigDecimalList(String path, List<BigDecimal> def) {
        List<BigDecimal> result = getBigDecimalList(path);

        if (result == null) {
            update(path, def, false);
        }

        return result == null ? def : result;
    }

    @Override @SuppressWarnings("unchecked")
    public void reload() throws IOException {
        if (file == null) {
            return;
        }
        @Cleanup FileInputStream stream = new FileInputStream(file);
        @Cleanup InputStreamReader reader = new InputStreamReader(stream);

        Gson gson = new Gson();
        values = (LinkedHashMap<String, Object>) yaml.get().loadAs(reader, LinkedHashMap.class);
        if (values == null) {
            values = new LinkedHashMap();
        }
    }

    @Override
    public void save() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        @Cleanup FileWriter fileWriter = new FileWriter(file);
        @Cleanup BufferedWriter writer = new BufferedWriter(fileWriter);

        this.yaml.get().dump(values, writer);
    }

    private void update(String path, Object value, Boolean overwrite) {
        Iterator<String> it = Arrays.asList(path.split("\\.")).iterator();

        if (!values.containsKey(path)) {
            values.put(path, value);
        } else if (values.containsKey(path) && overwrite) {
            values.put(path, value);
        }
    }

    @Override
    public ISection getSection(String section) {
        if (section.isEmpty()) {
            return this;
        }
        return new YamlSection(section, this);
    }

    @Override
    public void createSection(String section) {
        values.put(section, Maps.newHashMap());
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
}