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

package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.ReflectionUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.UnicodeTranslator;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils
{

    private static final Pattern timePattern = Pattern.compile( "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
            + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
            + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE );
    private static final Pattern HEX_PATTERN = Pattern.compile( "<#([A-Fa-f0-9]){6}>" );
    private static final Pattern GRADIENT_HEX_PATTERN = Pattern.compile( "(\\{(#[A-Fa-f0-9]{6})})(.+?)(\\{/(#[A-Fa-f0-9]{6})})" );
    private static final List<String> SPECIAL_COLORS = Arrays.asList( "&l", "&n", "&o", "&k", "&m" );
    private static final boolean IS_1_16;

    private static final Map<Color, ChatColor> COLORS = ImmutableMap.<Color, ChatColor>builder()
            .put( new Color( 0 ), ChatColor.getByChar( '0' ) )
            .put( new Color( 170 ), ChatColor.getByChar( '1' ) )
            .put( new Color( 43520 ), ChatColor.getByChar( '2' ) )
            .put( new Color( 43690 ), ChatColor.getByChar( '3' ) )
            .put( new Color( 11141120 ), ChatColor.getByChar( '4' ) )
            .put( new Color( 11141290 ), ChatColor.getByChar( '5' ) )
            .put( new Color( 16755200 ), ChatColor.getByChar( '6' ) )
            .put( new Color( 11184810 ), ChatColor.getByChar( '7' ) )
            .put( new Color( 5592405 ), ChatColor.getByChar( '8' ) )
            .put( new Color( 5592575 ), ChatColor.getByChar( '9' ) )
            .put( new Color( 5635925 ), ChatColor.getByChar( 'a' ) )
            .put( new Color( 5636095 ), ChatColor.getByChar( 'b' ) )
            .put( new Color( 16733525 ), ChatColor.getByChar( 'c' ) )
            .put( new Color( 16733695 ), ChatColor.getByChar( 'd' ) )
            .put( new Color( 16777045 ), ChatColor.getByChar( 'e' ) )
            .put( new Color( 16777215 ), ChatColor.getByChar( 'f' ) ).build();

    static
    {
        IS_1_16 = ReflectionUtils.getMethod( net.md_5.bungee.api.ChatColor.class, "of", String.class ) != null;
    }

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
            return "";
        }
        message = colorHex( message );

        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes( '&', message );
    }

    public static String colorHex( String message )
    {
        if ( !IS_1_16 )
        {
            return message;
        }
        Matcher matcher = HEX_PATTERN.matcher( message );
        while ( matcher.find() )
        {
            final net.md_5.bungee.api.ChatColor hexColor = net.md_5.bungee.api.ChatColor.of( matcher.group().substring( 1, matcher.group().length() - 1 ) );
            final String before = message.substring( 0, matcher.start() );
            final String after = message.substring( matcher.end() );

            message = before + hexColor + after;
            matcher = HEX_PATTERN.matcher( message );
        }
        message = colorGradients( message );
        return message;
    }

    private static String colorGradients( String message )
    {
        Matcher matcher = GRADIENT_HEX_PATTERN.matcher( message );
        while ( matcher.find() )
        {
            final String startColor = matcher.group( 2 );
            final String text = matcher.group( 3 );
            final String endColor = matcher.group( 5 );

            message = matcher.replaceFirst( applyGradient( text, startColor, endColor ).replace( "$", "\\$" ) );
            matcher = GRADIENT_HEX_PATTERN.matcher( message );
        }
        return message;
    }

    private static String applyGradient( String text, final String startHexColor, final String endHexColor )
    {
        final ChatColor startColor = ChatColor.of( startHexColor );
        final ChatColor endColor = ChatColor.of( endHexColor );

        final StringBuilder specialColors = new StringBuilder();
        for ( String color : SPECIAL_COLORS )
        {
            if ( text.contains( color ) )
            {
                specialColors.append( color );
                text = text.replace( color, "" );
            }
        }
        final StringBuilder stringBuilder = new StringBuilder();
        final ChatColor[] colors = getGradientColors( startColor.getColor(), endColor.getColor(), text.length() );
        final String[] characters = text.split( "" );

        for ( int i = 0; i < text.length(); i++ )
        {
            stringBuilder.append( colors[i] ).append( specialColors ).append( characters[i] );
        }
        return stringBuilder.toString();
    }

    private static ChatColor[] getGradientColors( final Color start, final Color end, final int step )
    {
        final ChatColor[] colors = new ChatColor[step];
        final int stepR = Math.abs( start.getRed() - end.getRed() ) / ( step - 1 );
        final int stepG = Math.abs( start.getGreen() - end.getGreen() ) / ( step - 1 );
        final int stepB = Math.abs( start.getBlue() - end.getBlue() ) / ( step - 1 );
        final int[] direction = new int[]{
                start.getRed() < end.getRed() ? +1 : -1,
                start.getGreen() < end.getGreen() ? +1 : -1,
                start.getBlue() < end.getBlue() ? +1 : -1
        };

        for ( int i = 0; i < step; i++ )
        {
            final Color color = new Color(
                    start.getRed() + ( ( stepR * i ) * direction[0] ),
                    start.getGreen() + ( ( stepG * i ) * direction[1] ),
                    start.getBlue() + ( ( stepB * i ) * direction[2] )
            );
            if ( IS_1_16 )
            {
                colors[i] = ChatColor.of( color );
            }
            else
            {
                colors[i] = getClosestColor( color );
            }
        }
        return colors;
    }

    private static ChatColor getClosestColor( Color color )
    {
        Color nearestColor = null;
        double nearestDistance = Integer.MAX_VALUE;

        for ( Color constantColor : COLORS.keySet() )
        {
            double distance = Math.pow( color.getRed() - constantColor.getRed(), 2 ) + Math.pow( color.getGreen() - constantColor.getGreen(), 2 ) + Math.pow( color.getBlue() - constantColor.getBlue(), 2 );
            if ( nearestDistance > distance )
            {
                nearestColor = constantColor;
                nearestDistance = distance;
            }
        }
        return COLORS.get( nearestColor );
    }

    public static String formatString( final User user, final String message )
    {
        return c( PlaceHolderAPI.formatMessage( user, message ) );
    }

    /**
     * Formats a message to TextComponent, translates color codes and replaces general placeholders.
     *
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static net.md_5.bungee.api.chat.BaseComponent[] format( String message )
    {
        return net.md_5.bungee.api.chat.TextComponent.fromLegacyText( c( UnicodeTranslator.translate( PlaceHolderAPI.formatMessage( message ) ) ) );
    }

    /**
     * Formats a message to TextComponent, translates color codes and replaces placeholders.
     *
     * @param messages The messages to be formatted.
     * @return The formatted message.
     */
    public static net.md_5.bungee.api.chat.BaseComponent[] format( List<String> messages )
    {
        return format( null, messages );
    }

    /**
     * Formats a message to TextComponent, translates color codes and replaces placeholders.
     *
     * @param user    The user for which the placeholders should be formatted.
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static net.md_5.bungee.api.chat.BaseComponent[] format( User user, String message )
    {
        return net.md_5.bungee.api.chat.TextComponent.fromLegacyText( formatString( user, message ) );
    }

    /**
     * Formats a message to TextComponent, translates color codes and replaces placeholders.
     *
     * @param user     The user for which the placeholders should be formatted.
     * @param messages The messages to be formatted.
     * @return The formatted message.
     */
    public static net.md_5.bungee.api.chat.BaseComponent[] format( User user, List<String> messages )
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
                .map( message -> new net.md_5.bungee.api.chat.BaseComponent[]{ new net.md_5.bungee.api.chat.TextComponent( message ) } )
                .flatMap( Arrays::stream )
                .toArray( net.md_5.bungee.api.chat.BaseComponent[]::new );
    }

    /**
     * Formats a message to TextComponent with given prefix.
     *
     * @param prefix  The prefix to be before the message.
     * @param message The message to be formatted.
     * @return The formatted message.
     */
    public static net.md_5.bungee.api.chat.BaseComponent[] format( String prefix, String message )
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
     * @param date           The date to be formatted.
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

    /**
     * Replaces placeholders in a string.
     *
     * @param message      the message to replace in
     * @param placeholders the placeholders with their values to be replaced
     * @return the message with the replaced placeholders.
     */
    public static String replacePlaceHolders( String message, Object... placeholders )
    {
        for ( int i = 0; i < placeholders.length - 1; i += 2 )
        {
            message = message.replace( placeholders[i].toString(), placeholders[i + 1].toString() );
        }
        message = PlaceHolderAPI.formatMessage( message );
        return message;
    }

    /**
     * Replaces placeholders in a string and formats using the placeholder api.
     *
     * @param user         the user to format placeholders for
     * @param message      the message to replace in
     * @param placeholders the placeholders with their values to be replaced
     * @return the message with the replaced placeholders.
     */
    public static String replacePlaceHolders( User user, String message, Object... placeholders )
    {
        for ( int i = 0; i < placeholders.length - 1; i += 2 )
        {
            message = message.replace( placeholders[i].toString(), placeholders[i + 1].toString() );
        }
        message = PlaceHolderAPI.formatMessage( user, message );
        return message;
    }

    /**
     * Replaces the last occurence of a keyword in the given string.
     *
     * @param string      the string to replace in
     * @param toReplace   the string to search on
     * @param replacement the string that should be used to replace
     * @return the given string with (possible) replacement done
     */
    public static String replaceLast( final String string, final String toReplace, final String replacement )
    {
        int pos = string.lastIndexOf( toReplace );
        if ( pos > -1 )
        {
            return string.substring( 0, pos )
                    + replacement
                    + string.substring( pos + toReplace.length() );
        }
        else
        {
            return string;
        }
    }

    /**
     * @param chars  the characters to use for the random string
     * @param length the length that the random string should be
     * @return a random string of the requested characters and length
     */
    public static String createRandomString( final String chars, final int length )
    {
        final StringBuilder sb = new StringBuilder();
        final Random random = new Random();

        for ( int i = 0; i < length; i++ )
        {
            sb.append( chars.charAt( random.nextInt( chars.length() ) ) );
        }

        return sb.toString();
    }

    /**
     * Checks if the server is a Bukkit instance or not
     *
     * @return true if Bukkit instance, false if not
     */
    public static boolean isSpigot()
    {
        return classFound( "org.bukkit.Bukkit" );
    }

    public static String getTimeLeft( final String format, final Date date )
    {
        return getTimeLeft( format, date.getTime() - System.currentTimeMillis() );
    }

    public static String getTimeLeft( final String format, final long millis )
    {
        return format
                .replace( "%days%", String.valueOf( getDays( millis ) ) )
                .replace( "%hours%", String.valueOf( getHours( millis ) ) )
                .replace( "%minutes%", String.valueOf( getMinutes( millis ) ) )
                .replace( "%seconds%", String.valueOf( getSeconds( millis ) ) );
    }

    private static long getDays( long time )
    {
        return TimeUnit.MILLISECONDS.toDays( time );
    }

    private static long getHours( long time )
    {
        time = time - TimeUnit.DAYS.toMillis( getDays( time ) );

        return TimeUnit.MILLISECONDS.toHours( time );
    }

    private static long getMinutes( long time )
    {
        time = time - TimeUnit.DAYS.toMillis( getDays( time ) );
        time = time - TimeUnit.HOURS.toMillis( getHours( time ) );

        return TimeUnit.MILLISECONDS.toMinutes( time );
    }

    private static long getSeconds( long time )
    {
        time = time - TimeUnit.DAYS.toMillis( getDays( time ) );
        time = time - TimeUnit.HOURS.toMillis( getHours( time ) );
        time = time - TimeUnit.MINUTES.toMillis( getMinutes( time ) );

        return TimeUnit.MILLISECONDS.toSeconds( time );
    }
}