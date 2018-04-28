package com.dbsoftwares.bungeeutilisals.api.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface ISection {

    /**
     * Used to check if a path is present or not.
     * @param path The path (sections separated by dots) you want to check.
     * @return True when the path is present, false if not.
     */
    Boolean exists(String path);

    /**
     * Used to set the value of the given path.
     * @param path The path you want to set the value of (separated by dots).
     * @param value The new value you want on this path.
     */
    void set(String path, Object value);

    /**
     * Gets the requested Object by path.
     * @param path The path of which you need the value.
     * @param <T> Type the value should be.
     * @return The value (object) bound to this path.
     */
    <T> T get(String path);

    /**
     * Gets the requested Object by path.
     *
     * @param path The path of which you need the value.
     * @param def  The default value for this path.
     * @param <T>  The type of the espected value.
     * @return The value (object) bound to this path.
     */
    <T> T get(String path, T def);

    /**
     * Used to check if the value bound to the given path is a String.
     * @param path The path you want to check.
     * @return True if the value is a String, false if not.
     */
    Boolean isString(String path);

    /**
     * Gets the requested String by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    String getString(String path);

    /**
     * Gets the requested String by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    String getString(String path, String def);

    /**
     * Used to check if the value bound to the given path is a Boolean.
     * @param path The path you want to check.
     * @return True if the value is a Boolean, false if not.
     */
    Boolean isBoolean(String path);

    /**
     * Gets the requested Boolean by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    Boolean getBoolean(String path);

    /**
     * Gets the requested Boolean by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    Boolean getBoolean(String path, Boolean def);

    /**
     * Used to check if the value bound to the given path is an Integer.
     * @param path The path you want to check.
     * @return True if the value is an Integer, false if not.
     */
    Boolean isInteger(String path);

    /**
     * Gets the requested Integer by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    Integer getInteger(String path);

    /**
     * Gets the requested Integer by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    Integer getInteger(String path, Integer def);

    /**
     * Used to check if the value bound to the given path is a Number.
     * @param path The path you want to check.
     * @return True if the value is a Number, false if not.
     */
    Boolean isNumber(String path);

    /**
     * Gets the requested Number by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    Number getNumber(String path);

    /**
     * Gets the requested Number by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    Number getNumber(String path, Number def);

    /**
     * Used to check if the value bound to the given path is a Double.
     * @param path The path you want to check.
     * @return True if the value is a Double, false if not.
     */
    Boolean isDouble(String path);

    /**
     * Gets the requested Double by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    Double getDouble(String path);

    /**
     * Gets the requested Double by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    Double getDouble(String path, Double def);

    /**
     * Used to check if the value bound to the given path is a Long.
     * @param path The path you want to check.
     * @return True if the value is a Long, false if not.
     */
    Boolean isLong(String path);

    /**
     * Gets the requested Long by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    Long getLong(String path);

    /**
     * Gets the requested Long by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    Long getLong(String path, Long def);

    /**
     * Used to check if the value bound to the given path is a Float.
     * @param path The path you want to check.
     * @return True if the value is a Float, false if not.
     */
    Boolean isFloat(String path);

    /**
     * Gets the requested Float by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    Float getFloat(String path);

    /**
     * Gets the requested Float by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    Float getFloat(String path, Float def);

    /**
     * Used to check if the value bound to the given path is a Byte.
     * @param path The path you want to check.
     * @return True if the value is a Byte, false if not.
     */
    Boolean isByte(String path);

    /**
     * Gets the requested Byte by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    Byte getByte(String path);

    /**
     * Gets the requested Byte by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    Byte getByte(String path, Byte def);

    /**
     * Used to check if the value bound to the given path is a Short.
     * @param path The path you want to check.
     * @return True if the value is a Short, false if not.
     */
    Boolean isShort(String path);

    /**
     * Gets the requested Short by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    Short getShort(String path);

    /**
     * Gets the requested Short by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    Short getShort(String path, Short def);

    /**
     * Used to check if the value bound to the given path is a BigInteger.
     * @param path The path you want to check.
     * @return True if the value is a BigInteger, false if not.
     */
    Boolean isBigInteger(String path);

    /**
     * Gets the requested BigInteger by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    BigInteger getBigInteger(String path);

    /**
     * Gets the requested BigInteger by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    BigInteger getBigInteger(String path, BigInteger def);

    /**
     * Used to check if the value bound to the given path is a BigDecimal.
     * @param path The path you want to check.
     * @return True if the value is a BigDecimal, false if not.
     */
    Boolean isBigDecimal(String path);

    /**
     * Gets the requested BigDecimal by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    BigDecimal getBigDecimal(String path);

    /**
     * Gets the requested BigDecimal by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    BigDecimal getBigDecimal(String path, BigDecimal def);

    /**
     * Used to check if the value bound to the given path is a List.
     * @param path The path you want to check.
     * @return True if the value is a List, false if not.
     */
    Boolean isList(String path);

    /**
     * Gets the requested List by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    List getList(String path);

    /**
     * Gets the requested List by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    List getList(String path, List def);

    /**
     * Gets the requested List by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    List<String> getStringList(String path);

    /**
     * Gets the requested List by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    List<String> getStringList(String path, List<String> def);

    /**
     * Gets the requested List by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    List<Integer> getIntegerList(String path);

    /**
     * Gets the requested List by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    List<Integer> getIntegerList(String path, List<Integer> def);

    /**
     * Gets the requested List by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    List<Double> getDoubleList(String path);

    /**
     * Gets the requested List by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    List<Double> getDoubleList(String path, List<Double> def);

    /**
     * Gets the requested List by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    List<Boolean> getBooleanList(String path);

    /**
     * Gets the requested List by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    List<Boolean> getBooleanList(String path, List<Boolean> def);

    /**
     * Gets the requested List by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    List<Long> getLongList(String path);

    /**
     * Gets the requested List by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    List<Long> getLongList(String path, List<Long> def);

    /**
     * Gets the requested List by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    List<Byte> getByteList(String path);

    /**
     * Gets the requested List by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    List<Byte> getByteList(String path, List<Byte> def);

    /**
     * Gets the requested List by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    List<Short> getShortList(String path);

    /**
     * Gets the requested List by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    List<Short> getShortList(String path, List<Short> def);

    /**
     * Gets the requested List by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    List<Float> getFloatList(String path);

    /**
     * Gets the requested List by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    List<Float> getFloatList(String path, List<Float> def);

    /**
     * Gets the requested List by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    List<Number> getNumberList(String path);

    /**
     * Gets the requested List by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    List<Number> getNumberList(String path, List<Number> def);

    /**
     * Gets the requested List by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    List<BigInteger> getBigIntegerList(String path);

    /**
     * Gets the requested List by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    List<BigInteger> getBigIntegerList(String path, List<BigInteger> def);

    /**
     * Gets the requested List by path.
     * @param path The path of which you need the value.
     * @return The value bound to this path.
     */
    List<BigDecimal> getBigDecimalList(String path);

    /**
     * Gets the requested List by path, sets default if not present.
     * @param path The path of which you need the value.
     * @param def The default value this path should get if not present.
     * @return The value bound to this path, default if not present.
     */
    List<BigDecimal> getBigDecimalList(String path, List<BigDecimal> def);

    /**
     * Used to get a new IConfiguration representing the section you requested.
     * @param section The section you want to get.
     * @return A new IConfiguration representing the requested Section.
     */
    ISection getSection(String section);

    /**
     * Creates an empty section.
     * @param section The path for the new empty section.
     */
    void createSection(String section);

    /**
     * @return A set containing all keys of the IConfiguration.
     */
    Set<String> getKeys();

    /**
     * @param path The path from which you want to retrieve the keys.
     * @return A set containing all keys of the IConfiguration.
     */
    Set<String> getKeys(String path);
}