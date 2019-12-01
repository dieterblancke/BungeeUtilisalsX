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
import com.dbsoftwares.bungeeutilisals.api.utils.text.UnicodeTranslator;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.AbstractPacketHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils
{

    private static final Pattern timePattern = Pattern.compile( "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
            + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
            + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE );

    private Utils()
    {
    }

    /**
     * Formats a message.
     *
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static String c( String message )
    {
        if ( message == null )
        {
            return message;
        }
        return ChatColor.translateAlternateColorCodes( '&', message );
    }

    /**
     * Formats a message to TextComponent, translates color codes and replaces general placeholders.
     *
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static BaseComponent[] format( String message )
    {
        return TextComponent.fromLegacyText( c( UnicodeTranslator.translate( PlaceHolderAPI.formatMessage( message ) ) ) );
    }

    /**
     * Formats a message to TextComponent, translates color codes and replaces placeholders.
     *
     * @param messages The messages to be formatted.
     * @return The formatted message.
     */
    public static BaseComponent[] format( List<String> messages )
    {
        return format( (User) null, messages );
    }

    /**
     * Formats a message to TextComponent, translates color codes and replaces placeholders.
     *
     * @param player  The player for which the placeholders should be formatted.
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static BaseComponent[] format( ProxiedPlayer player, String message )
    {
        return format( BUCore.getApi().getUser( player ).orElse( null ), message );
    }

    /**
     * Formats a message to TextComponent, translates color codes and replaces placeholders.
     *
     * @param player   The player for which the placeholders should be formatted.
     * @param messages The messages to be formatted.
     * @return The formatted message.
     */
    public static BaseComponent[] format( ProxiedPlayer player, List<String> messages )
    {
        return format( BUCore.getApi().getUser( player ).orElse( null ), messages );
    }

    /**
     * Formats a message to TextComponent, translates color codes and replaces placeholders.
     *
     * @param user    The user for which the placeholders should be formatted.
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static BaseComponent[] format( User user, String message )
    {
        return TextComponent.fromLegacyText( c( PlaceHolderAPI.formatMessage( user, message ) ) );
    }

    /**
     * Formats a message to TextComponent, translates color codes and replaces placeholders.
     *
     * @param user     The user for which the placeholders should be formatted.
     * @param messages The messages to be formatted.
     * @return The formatted message.
     */
    public static BaseComponent[] format( User user, List<String> messages )
    {
        final AtomicInteger count = new AtomicInteger();
        return messages
                .stream()
                .map( message ->
                {
                    if ( count.incrementAndGet() >= messages.size() )
                    {
                        return c( PlaceHolderAPI.formatMessage( user, message ) );
                    }
                    return c( PlaceHolderAPI.formatMessage( user, message + "\n" ) );
                } )
                .map( message -> new BaseComponent[]{ new TextComponent( message ) } )
                .flatMap( Arrays::stream )
                .toArray( BaseComponent[]::new );
    }

    /**
     * Formats a message to TextComponent with given prefix.
     *
     * @param prefix  The prefix to be before the message.
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static BaseComponent[] format( String prefix, String message )
    {
        return format( prefix + message );
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
    public static <K, V> K getKeyFromValue( Map<K, V> map, V value )
    {
        for ( Map.Entry<K, V> entry : map.entrySet() )
        {
            if ( entry.getValue().equals( value ) )
            {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * @return The current date (dd-MM-yyyy)
     */
    public static String getCurrentDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat( "dd-MM-yyyy" );
        return sdf.format( new Date( System.currentTimeMillis() ) );
    }

    /**
     * @return The current time (kk:mm:ss)
     */
    public static String getCurrentTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat( "kk:mm:ss" );
        return sdf.format( new Date( System.currentTimeMillis() ) );
    }

    /**
     * @param stream The stream you want to read.
     * @return A list containing all lines from the input stream.
     */
    public static List<String> readFromStream( InputStream stream )
    {
        final List<String> lines = Lists.newArrayList();

        try ( InputStream input = stream;
              InputStreamReader inputStreamReader = new InputStreamReader( input );
              BufferedReader reader = new BufferedReader( inputStreamReader ) )
        {
            reader.lines().forEach( lines::add );
        }
        catch ( IOException ignored )
        {
            // ignored
        }

        return lines;
    }

    /**
     * Attempts to parse a long time from a given string.
     *
     * @param time The string you want to importer to time.
     * @return The time, in MILLIS, you requested.
     */
    public static long parseDateDiff( String time )
    {
        try
        {
            Matcher m = timePattern.matcher( time );
            int years = 0;
            int months = 0;
            int weeks = 0;
            int days = 0;
            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            boolean found = false;
            while ( m.find() )
            {
                if ( m.group() == null || m.group().isEmpty() )
                {
                    continue;
                }
                for ( int i = 0; i < m.groupCount(); i++ )
                {
                    if ( m.group( i ) != null && !m.group( i ).isEmpty() )
                    {
                        found = true;
                        break;
                    }
                }
                if ( found )
                {
                    if ( m.group( 1 ) != null && !m.group( 1 ).isEmpty() )
                    {
                        years = Integer.parseInt( m.group( 1 ) );
                    }
                    if ( m.group( 2 ) != null && !m.group( 2 ).isEmpty() )
                    {
                        months = Integer.parseInt( m.group( 2 ) );
                    }
                    if ( m.group( 3 ) != null && !m.group( 3 ).isEmpty() )
                    {
                        weeks = Integer.parseInt( m.group( 3 ) );
                    }
                    if ( m.group( 4 ) != null && !m.group( 4 ).isEmpty() )
                    {
                        days = Integer.parseInt( m.group( 4 ) );
                    }
                    if ( m.group( 5 ) != null && !m.group( 5 ).isEmpty() )
                    {
                        hours = Integer.parseInt( m.group( 5 ) );
                    }
                    if ( m.group( 6 ) != null && !m.group( 6 ).isEmpty() )
                    {
                        minutes = Integer.parseInt( m.group( 6 ) );
                    }
                    if ( m.group( 7 ) != null && !m.group( 7 ).isEmpty() )
                    {
                        seconds = Integer.parseInt( m.group( 7 ) );
                    }
                    break;
                }
            }
            if ( !found )
            {
                return 0;
            }
            if ( years > 25 )
            {
                return 0;
            }
            Calendar c = new GregorianCalendar();
            if ( years > 0 )
            {
                c.add( Calendar.YEAR, years );
            }
            if ( months > 0 )
            {
                c.add( Calendar.MONTH, months );
            }
            if ( weeks > 0 )
            {
                c.add( Calendar.WEEK_OF_YEAR, weeks );
            }
            if ( days > 0 )
            {
                c.add( Calendar.DAY_OF_MONTH, days );
            }
            if ( hours > 0 )
            {
                c.add( Calendar.HOUR_OF_DAY, hours );
            }
            if ( minutes > 0 )
            {
                c.add( Calendar.MINUTE, minutes );
            }
            if ( seconds > 0 )
            {
                c.add( Calendar.SECOND, seconds );
            }
            return c.getTimeInMillis();
        }
        catch ( NumberFormatException e )
        {
            return 0;
        }
    }

    /**
     * Checks if given parameter is a Boolean.
     *
     * @param object The object you want to check.
     * @return True if Boolean, false if not.
     */
    public static boolean isBoolean( Object object )
    {
        try
        {
            Boolean.parseBoolean( object.toString() );
            return true;
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    /**
     * Capitalizes first letter of every word found.
     *
     * @param words The string you want to capitalize.
     * @return A new capitalized String.
     */
    public static String capitalizeWords( String words )
    {
        if ( words != null && words.length() != 0 )
        {
            char[] chars = words.toCharArray();
            char[] newCharacters = new char[chars.length];

            char lastChar = ' ';
            for ( int i = 0; i < chars.length; i++ )
            {
                char character = chars[i];

                if ( lastChar == ' ' )
                {
                    newCharacters[i] = Character.toUpperCase( character );
                }
                else
                {
                    newCharacters[i] = character;
                }
            }

            return new String( newCharacters );
        }
        else
        {
            return words;
        }
    }

    /**
     * Returns UserConnection bound to PacketHandler if instance of Downstream- or upstream bridge.
     *
     * @param handler The handler
     * @return UserConnection from handler.
     */
    public static String getName( AbstractPacketHandler handler )
    {
        try
        {
            if ( handler.getClass().getSimpleName().equalsIgnoreCase( "DownstreamBridge" )
                    || handler.getClass().getSimpleName().equalsIgnoreCase( "UpstreamBridge" ) )
            {
                Object userConn = ReflectionUtils.getField( handler.getClass(), "con" ).get( handler );
                Method getName = ReflectionUtils.getMethod( userConn.getClass(), "getName" );

                return (String) getName.invoke( userConn );
            }
        }
        catch ( InvocationTargetException | IllegalAccessException ignored )
        {
            // ignored
        }
        return null;
    }

    /**
     * Converts a InetSocketAddress into a String IPv4.
     *
     * @param a The address to be converted.
     * @return The converted address as a String.
     */
    public static String getIP( InetSocketAddress a )
    {
        return getIP( a.getAddress() );
    }

    /**
     * Converts a InetAddress into a String IPv4.
     *
     * @param a The address to be converted.
     * @return The converted address as a String.
     */
    public static String getIP( InetAddress a )
    {
        return a.toString().split( "/" )[1].split( ":" )[0];
    }

    /**
     * Formatting a list into a string with given seperators.
     *
     * @param objects   Iterable which has to be converted.
     * @param separator Seperator which will be used to seperate the list.
     * @return A string in which all sendable of the list are seperated by the separator.
     */
    public static String formatList( Iterable<?> objects, String separator )
    {
        if ( objects == null )
        {
            return null;
        }
        return Utils.c( Joiner.on( separator ).join( objects ) );
    }

    /**
     * Similar to {@link #formatList(Iterable, String)} but for Arrays.
     *
     * @param objects   Array which has to be converted.
     * @param separator Seperator which will be used to seperate the array.
     * @return A string in which all sendable of the array are seperated by the separator.
     */
    public static String formatList( Object[] objects, String separator )
    {
        if ( objects == null )
        {
            return null;
        }
        return Utils.c( Joiner.on( separator ).join( objects ) );
    }

    /**
     * Checks if a class is present or not.
     *
     * @param clazz The class to be checked.
     * @return True if found, false if not.
     */
    public static boolean classFound( String clazz )
    {
        try
        {
            Class.forName( clazz );
            return true;
        }
        catch ( ClassNotFoundException e )
        {
            return false;
        }
    }

    /**
     * Formats current time into the following date format: "dd-MM-yyyy kk:mm:ss"
     *
     * @return a formatted date string.
     */
    public static String getFormattedDate()
    {
        return formatDate( new Date( System.currentTimeMillis() ) );
    }

    /**
     * Formats current date into a custom date format.
     *
     * @param format The date format to be used.
     * @return a formatted date string.
     */
    public static String getFormattedDate( String format )
    {
        return formatDate( format, new Date( System.currentTimeMillis() ) );
    }

    /**
     * Formats the given date into the following format: "dd-MM-yyyy kk:mm:ss"
     *
     * @param date The date to be formatted.
     * @return a formatted date string.
     */
    public static String formatDate( Date date )
    {
        SimpleDateFormat sdf = new SimpleDateFormat( "dd-MM-yyyy kk:mm:ss" );
        return sdf.format( date );
    }

    /**
     * Formats the given date into the following format: "dd-MM-yyyy kk:mm:ss"
     *
     * @param date The date to be formatted.
     * @param languageConfig The config to take the date format from
     * @return a formatted date string.
     */
    public static String formatDate( Date date, IConfiguration languageConfig )
    {
        return formatDate( languageConfig.getString( "date-format" ), date );
    }

    /**
     * Formats a given date into the given format.
     *
     * @param format The date format to be used.
     * @param date   The date to be formatted.
     * @return a formatted date string.
     */
    public static String formatDate( String format, Date date )
    {
        SimpleDateFormat sdf = new SimpleDateFormat( format );
        return sdf.format( date );
    }

    /**
     * Executes enum valueOf, if that throws an error, a default is returned.
     *
     * @param name The name to be used.
     * @param def  The default value.
     * @param <T>  The enum type.
     * @return Parsed enum or default.
     */
    public static <T extends Enum<T>> T valueOfOr( final String name, T def )
    {
        assert def != null : "Default value cannot be null.";

        return valueOfOr( (Class<T>) def.getClass(), name, def );
    }

    /**
     * Executes enum valueOf, if that throws an error, a default is returned.
     *
     * @param clazz The enum class
     * @param name  The name to be used.
     * @param def   The default value.
     * @param <T>   The enum type.
     * @return Parsed enum or default.
     */
    public static <T extends Enum<T>> T valueOfOr( final Class<T> clazz, final String name, T def )
    {
        try
        {
            T value = Enum.valueOf( clazz, name );

            return value == null ? def : value;
        }
        catch ( IllegalArgumentException e )
        {
            return def;
        }
    }

    /**
     * Reads UUID from string, if it's undashed, it will add these.
     *
     * @param str The UUID to be formatted
     * @return UUID object of the entered uuid
     */
    public static UUID readUUIDFromString( String str )
    {
        try
        {
            return UUID.fromString( str );
        }
        catch ( IllegalArgumentException e )
        {
            return UUID.fromString(
                    str.replaceFirst(
                            "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                            "$1-$2-$3-$4-$5"
                    )
            );
        }
    }

    /**
     * Copies all elements from the iterable collection of originals to the
     * collection provided.
     *
     * @param <T>        the collection of strings
     * @param token      String to search for
     * @param originals  An iterable collection of strings to filter.
     * @param collection The collection to add matches to
     * @return the collection provided that would have the elements copied
     * into
     * @throws UnsupportedOperationException if the collection is immutable
     *                                       and originals contains a string which starts with the specified
     *                                       search string.
     * @throws IllegalArgumentException      if any parameter is is null
     * @throws IllegalArgumentException      if originals contains a null element.
     */
    public static <T extends Collection<? super String>> T copyPartialMatches( final String token, final Iterable<String> originals, final T collection ) throws UnsupportedOperationException, IllegalArgumentException
    {
        for ( String string : originals )
        {
            if ( startsWithIgnoreCase( string, token ) )
            {
                collection.add( string );
            }
        }

        return collection;
    }

    /**
     * This method uses a region to check case-insensitive equality. This
     * means the internal array does not need to be copied like a
     * toLowerCase() call would.
     *
     * @param string String to check
     * @param prefix Prefix of string to compare
     * @return true if provided string starts with, ignoring case, the prefix
     * provided
     * @throws NullPointerException     if prefix is null
     * @throws IllegalArgumentException if string is null
     */
    public static boolean startsWithIgnoreCase( final String string, final String prefix ) throws IllegalArgumentException, NullPointerException
    {
        if ( string.length() < prefix.length() )
        {
            return false;
        }
        return string.regionMatches( true, 0, prefix, 0, prefix.length() );
    }
}