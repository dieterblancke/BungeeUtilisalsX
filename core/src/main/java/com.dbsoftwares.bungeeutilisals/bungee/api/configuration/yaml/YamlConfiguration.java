package com.dbsoftwares.bungeeutilisals.bungee.api.configuration.yaml;

import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.configuration.ISection;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.math.MathUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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

    LinkedHashMap<String, Object> self = Maps.newLinkedHashMap();
    private File file;
    private final ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>() {
        protected Yaml initialValue() {
            Representer representer = new Representer() {
                {
                    this.representers.put(YamlConfiguration.class, data -> represent(self));
                }
            };
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setAllowUnicode(true);
            return new Yaml(new Constructor(), representer, options);
        }
    };

    public YamlConfiguration(File file) throws IOException {
        this(file.exists() ? new FileInputStream(file) : null);
        this.file = file;
    }

    @SuppressWarnings("unchecked")
    public YamlConfiguration(InputStream input) throws IOException {
        if (input == null) {
            return;
        }
        InputStreamReader reader = new InputStreamReader(input);
        LinkedHashMap<String, Object> values = (LinkedHashMap<String, Object>) yaml.get().loadAs(reader, LinkedHashMap.class);
        if (values == null) {
            values = new LinkedHashMap<>();
        }

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey() == null ? "null" : entry.getKey();

            if (entry.getValue() instanceof Map) {
                this.self.put(key, new YamlSection((Map<String, Object>) entry.getValue()));
            } else if (entry.getValue() instanceof List) {
                List list = (List) entry.getValue();
                List<ISection> sections = Lists.newArrayList();

                for (Object object : list) {
                    if (object instanceof Map) {
                        sections.add(new YamlSection((Map<String, Object>) object));
                    } else {
                        sections.clear();
                        this.self.put(key, entry.getValue());
                        break;
                    }
                }

                if (!sections.isEmpty()) {
                    this.self.put(key, sections);
                }
            } else {
                this.self.put(key, entry.getValue());
            }
        }

        input.close();
        reader.close();
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
    public boolean exists(String path) {
        return self.containsKey(path);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(String path, Object value) {
        if (value instanceof Map) {
            value = new YamlSection((Map<String, Object>) value);
        }

        ISection section = this.getSectionFor(path);
        if (section == this) {
            if (value == null) {
                this.self.remove(path);
            } else {
                this.self.put(path, value);
            }
        } else {
            section.set(getChild(path), value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String path, T def) {
        ISection section = getSectionFor(path);
        Object value;
        if (section == this) {
            value = this.self.get(path);
        } else {
            value = section.get(getChild(path));
        }
        return value != null ? (T) value : def;
    }

    @Override
    public <T> T get(String path) {
        return get(path, null);
    }

    @Override
    public boolean isString(String path) {
        Object object = get(path);
        return object instanceof String;
    }

    @Override
    public String getString(String path) {
        return get(path);
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
    public boolean isBoolean(String path) {
        Object object = get(path);
        return object instanceof Boolean;
    }

    @Override
    public boolean getBoolean(String path) {
        return get(path);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        Boolean result = getBoolean(path);

        if (result == null) {
            update(path, def, false);
        }
        return result == null ? def : result;
    }

    @Override
    public boolean isInteger(String path) {
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
    public boolean isNumber(String path) {
        Object object = get(path);
        return object instanceof Number;
    }

    @Override
    public Number getNumber(String path) {
        return get(path);
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
    public boolean isDouble(String path) {
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
    public boolean isLong(String path) {
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
    public boolean isFloat(String path) {
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
    public boolean isByte(String path) {
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
    public boolean isShort(String path) {
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
    public boolean isBigInteger(String path) {
        Object object = get(path);
        return object instanceof BigInteger;
    }

    @Override
    public BigInteger getBigInteger(String path) {
        return get(path);
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
    public boolean isBigDecimal(String path) {
        Object object = get(path);
        return object instanceof BigDecimal;
    }

    @Override
    public BigDecimal getBigDecimal(String path) {
        return get(path);
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
    public boolean isList(String path) {
        Object object = get(path);
        return object instanceof List;
    }

    @Override
    public List getList(String path) {
        return get(path);
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
    public List<ISection> getSectionList(String path) {
        List list = getList(path);

        if (list == null) {
            return null;
        }
        List<ISection> sections = Lists.newArrayList();
        for (Object object : list) {
            if (object instanceof ISection) {
                sections.add((ISection) object);
            }
        }
        return sections;
    }

    @Override @SuppressWarnings("unchecked")
    public void reload() throws IOException {
        if (file == null) {
            return;
        }
        FileInputStream stream = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(stream);

        LinkedHashMap<String, Object> values = (LinkedHashMap<String, Object>) yaml.get().loadAs(reader, LinkedHashMap.class);
        if (values == null) {
            values = new LinkedHashMap<>();
        }

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey() == null ? "null" : entry.getKey();

            if (entry.getValue() instanceof Map) {
                this.self.put(key, new YamlSection((Map<String, Object>) entry.getValue()));
            } else {
                this.self.put(key, entry.getValue());
            }
        }

        reader.close();
        stream.close();
    }

    @Override
    public void save() throws IOException {
        @Cleanup FileWriter fileWriter = new FileWriter(file);
        @Cleanup BufferedWriter writer = new BufferedWriter(fileWriter);

        this.yaml.get().dump(self, writer);
    }

    private void update(String path, Object value, Boolean overwrite) {
        if (!self.containsKey(path)) {
            self.put(path, value);
        } else if (self.containsKey(path) && overwrite) {
            self.put(path, value);
        }
    }

    @Override
    public ISection getSection(String path) {
        if (path.isEmpty()) {
            return this;
        }
        return this.get(path, new YamlSection(Maps.newLinkedHashMap()));
    }

    @Override
    public void createSection(String section) {
        self.put(section, new YamlSection(Maps.newLinkedHashMap()));
    }

    @Override
    public Set<String> getKeys() {
        return self.keySet();
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

    private ISection getSectionFor(String path) {
        int index = path.indexOf(46);
        if (index == -1) {
            return this;
        } else {
            String root = path.substring(0, index);
            Object section = this.self.get(root);
            if (section == null) {
                section = new YamlSection(Maps.newLinkedHashMap());
                this.self.put(root, section);
            }

            return (YamlSection) section;
        }
    }

    private String getChild(String path) {
        int index = path.indexOf(46);
        return index == -1 ? path : path.substring(index + 1);
    }
}