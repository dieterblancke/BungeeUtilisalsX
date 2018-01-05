package com.dbsoftwares.bungeeutilisals.api.utils;

/*
 * Created by DBSoftwares on 03 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

import com.google.common.collect.Lists;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.connection.UpstreamBridge;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
                    + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
                    + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
            Pattern.CASE_INSENSITIVE);

    /**
     * Formats a message.
     *
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static String c(String message) {
        if (message == null) {
            return message;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Formats a message to TextComponent.
     *
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static BaseComponent[] format(String message) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message));
    }

    /**
     * Formats a message to TextComponent with given prefix.
     *
     * @param prefix  The prefix to be before the message.
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static BaseComponent[] format(String prefix, String message) {
        return format(prefix + message);
    }

    /**
     * Util to get a key from value in a map.
     *
     * @param map   The map to get a key by value.
     * @param value The value to get thekey from.
     * @param <K>   The key type.
     * @param <V>   The value type
     * @return The key bound to the requested value.
     */
    public static <K, V> K getKeyFromValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * @return The current date (dd-MM-yyyy)
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    /**
     * @return The current time (kk:mm:ss)
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    /**
     * @param stream The stream you want to read.
     * @return A list containing all lines from the input stream.
     */
    public static List<String> readFromStream(InputStream stream) {
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        List<String> lines = Lists.newArrayList();
        reader.lines().forEach(lines::add);

        try {
            reader.close();
        } catch (IOException ignored) {
        }
        try {
            inputStreamReader.close();
        } catch (IOException ignored) {
        }
        try {
            stream.close();
        } catch (IOException ignored) {
        }

        return lines;
    }

    /**
     * Attempts to parse a long time from a given string.
     *
     * @param time The string you want to convert to time.
     * @return The time, in millis, you requested.
     */
    public static long parseDateDiff(String time) {
        Matcher m = timePattern.matcher(time);
        Integer years = 0, months = 0, weeks = 0, days = 0, hours = 0, minutes = 0, seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty()) {
                    years = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty()) {
                    months = Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty()) {
                    weeks = Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty()) {
                    days = Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty()) {
                    hours = Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty()) {
                    minutes = Integer.parseInt(m.group(6));
                }
                if (m.group(7) != null && !m.group(7).isEmpty()) {
                    seconds = Integer.parseInt(m.group(7));
                }
                break;
            }
        }
        if (!found) {
            return 0;
        }
        if (years > 20) {
            return 0;
        }
        Calendar c = new GregorianCalendar();
        if (years > 0) {
            c.add(Calendar.YEAR, years);
        }
        if (months > 0) {
            c.add(Calendar.MONTH, months);
        }
        if (weeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, weeks);
        }
        if (days > 0) {
            c.add(Calendar.DAY_OF_MONTH, days);
        }
        if (hours > 0) {
            c.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes > 0) {
            c.add(Calendar.MINUTE, minutes);
        }
        if (seconds > 0) {
            c.add(Calendar.SECOND, seconds);
        }
        return c.getTimeInMillis();
    }

    /**
     * Checks if given parameter is a Boolean.
     *
     * @param object The object you want to check.
     * @return True if Boolean, false if not.
     */
    public static boolean isBoolean(Object object) {
        try {
            Boolean.parseBoolean(object.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Capitalizes first letter of every word found.
     *
     * @param words The string you want to capitalize.
     * @return A new capitalized String.
     */
    public static String capitalizeWords(String words) {
        if (words != null && words.length() != 0) {
            char[] chars = words.toCharArray();
            char[] newCharacters = new char[chars.length];

            char lastChar = ' ';
            for (int i = 0; i < chars.length; i++) {
                char character = chars[i];

                if (lastChar == ' ') {
                    newCharacters[i] = Character.toUpperCase(character);
                } else {
                    newCharacters[i] = character;
                }
            }

            return new String(newCharacters);
        } else {
            return words;
        }
    }

    /**
     * Returns UserConnection bound to PacketHandler if instance of Downstream- or upstream bridge.
     *
     * @param handler The handler
     * @return UserConnection from handler.
     */
    public static UserConnection getConnection(AbstractPacketHandler handler) {
        try {
            if (handler instanceof DownstreamBridge) {
                DownstreamBridge bridge = (DownstreamBridge) handler;
                return (UserConnection) ReflectionUtils.getField(bridge.getClass(), "con").get(bridge);
            }

            if (handler instanceof UpstreamBridge) {
                UpstreamBridge bridge = (UpstreamBridge) handler;
                return (UserConnection) ReflectionUtils.getField(bridge.getClass(), "con").get(bridge);
            }
        } catch (IllegalAccessException ignored) {
        }
        return null;
    }

    /**
     * Registers a packet.
     *
     * @param direction   The packet direction.
     * @param id          The packet ID.
     * @param packetClass The packet class.
     * @return True if successfull, false if not.
     */
    public static boolean registerPacket(Protocol.DirectionData direction, int protocol, int id, Class<? extends DefinedPacket> packetClass) {
        try {
            Class<?> protocolMap = Class.forName("net.md_5.bungee.protocol.Protocol$ProtocolMapping");

            Method register = Protocol.DirectionData.class.getDeclaredMethod("registerPacket", Class.class,
                    Array.newInstance(protocolMap, 0).getClass());
            Method map = Protocol.class.getDeclaredMethod("map", int.class, int.class);
            register.setAccessible(true);
            map.setAccessible(true);

            Object packetMap = Array.newInstance(protocolMap, 1);
            Array.set(packetMap, 0, map.invoke(null, protocol, id));
            register.invoke(direction, packetClass, packetMap);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}