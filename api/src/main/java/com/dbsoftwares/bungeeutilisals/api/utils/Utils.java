/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.api.utils;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.ReflectionUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.connection.UpstreamBridge;
import net.md_5.bungee.protocol.AbstractPacketHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private final static Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
                    + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
                    + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
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
     * Formats a message to TextComponent, translates color codes and replaces general placeholders.
     *
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static BaseComponent[] format(String message) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', PlaceHolderAPI.formatMessage(message)));
    }

    /**
     * Formats a message to TextComponent, translates color codes and replaces placeholders.
     *
     * @param player  The player for which the placeholders should be formatted.
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static BaseComponent[] format(ProxiedPlayer player, String message) {
        return format(BUCore.getApi().getUser(player).orElse(null), message);
    }

    /**
     * Formats a message to TextComponent, translates color codes and replaces placeholders.
     *
     * @param user    The user for which the placeholders should be formatted.
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static BaseComponent[] format(User user, String message) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', PlaceHolderAPI.formatMessage(user, message)));
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
     * @param time The string you want to importer to time.
     * @return The time, in millis, you requested.
     */
    public static long parseDateDiff(String time) {
        try {
            Matcher m = timePattern.matcher(time);
            int years = 0, months = 0, weeks = 0, days = 0, hours = 0, minutes = 0, seconds = 0;
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
            if (years > 25) {
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
        } catch (NumberFormatException e) {
            return 0;
        }
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
     * Converts a InetSocketAddress into a String IPv4.
     *
     * @param a The address to be converted.
     * @return The converted address as a String.
     */
    public static String getIP(InetSocketAddress a) {
        return getIP(a.getAddress());
    }

    /**
     * Converts a InetAddress into a String IPv4.
     *
     * @param a The address to be converted.
     * @return The converted address as a String.
     */
    public static String getIP(InetAddress a) {
        return a.toString().split("/")[1].split(":")[0];
    }

    /**
     * Formatting a list into a string with given seperators.
     *
     * @param objects    Iterable which has to be converted.
     * @param separators Seperator which will be used to seperate the list.
     * @return A string in which all sendable of the list are seperated by the separator.
     */
    public static String formatList(Iterable<?> objects, String separators) {
        return Utils.c(Joiner.on(separators).join(objects));
    }

    /**
     * Similar to {@link #formatList(Iterable, String)} but for Arrays.
     *
     * @param objects    Array which has to be converted.
     * @param separators Seperator which will be used to seperate the array.
     * @return A string in which all sendable of the array are seperated by the separator.
     */
    public static String formatList(Object[] objects, String separators) {
        return Utils.c(Joiner.on(separators).join(objects));
    }

    /**
     * Checks if a class is present or not.
     *
     * @param clazz The class to be checked.
     * @return True if found, false if not.
     */
    public static boolean classFound(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Formats current time into the following date format: "dd-MM-yyyy kk:mm:ss"
     *
     * @return a formatted date string.
     */
    public static String getFormattedDate() {
        return formatDate(new Date(System.currentTimeMillis()));
    }

    /**
     * Formats current date into a custom date format.
     *
     * @param format The date format to be used.
     * @return a formatted date string.
     */
    public static String getFormattedDate(String format) {
        return formatDate(format, new Date(System.currentTimeMillis()));
    }

    /**
     * Formats the given date into the following format: "dd-MM-yyyy kk:mm:ss"
     *
     * @param date The date to be formatted.
     * @return a formatted date string.
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
        return sdf.format(date);
    }

    /**
     * Formats a given date into the given format.
     *
     * @param format The date format to be used.
     * @param date   The date to be formatted.
     * @return a formatted date string.
     */
    public static String formatDate(String format, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
}