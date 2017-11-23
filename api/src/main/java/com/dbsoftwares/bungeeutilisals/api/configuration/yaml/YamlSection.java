package com.dbsoftwares.bungeeutilisals.api.configuration.yaml;

import com.dbsoftwares.bungeeutilisals.api.configuration.ISection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public class YamlSection implements ISection {

    String prefix;
    ISection parent;

    public YamlSection(String prefix, ISection parent) {
        this.prefix = prefix;
        this.parent = parent;
    }

    @Override
    public Boolean exists(String path) {
        return parent.exists(getPath(path));
    }

    @Override
    public void set(String path, Object value) {
        parent.set(getPath(path), value);
    }

    @Override
    public Object get(String path) {
        return parent.get(getPath(path));
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
        return parent.getKeys(prefix);
    }

    @Override
    public Set<String> getKeys(String path) {
        return parent.getKeys(getPath(path));
    }

    private String getPath(String path) {
        return prefix + "." + path;
    }
}