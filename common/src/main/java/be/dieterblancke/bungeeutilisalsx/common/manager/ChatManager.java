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

package be.dieterblancke.bungeeutilisalsx.common.manager;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.UnicodeTranslator;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ChatManager
{

    private static final Pattern IP_PATTERN = Pattern.compile( "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$" );
    private static final Pattern WEB_PATTERN = Pattern.compile( "^((https?|ftp)://|(www|ftp)\\.)?[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)+([/?].*)?$" );

    private final List<Pattern> swearPatterns = Lists.newArrayList();
    private final Map<String, String> utfSymbols = Maps.newHashMap();

    public ChatManager()
    {
        reload();
    }

    public boolean checkForAdvertisement( final User user, String message )
    {
        final IConfiguration config = ConfigFiles.ANTIAD.getConfig();
        if ( !config.getBoolean( "enabled" ) || user.hasPermission( config.getString( "bypass" ) ) )
        {
            return false;
        }

        message = Normalizer.normalize( message, Normalizer.Form.NFKD );
        message = message.replaceAll( "[^\\x00-\\x7F]", "" );
        message = message.replace( ",", "." );

        for ( String word : message.split( " " ) )
        {
            if ( config.getStringList( "allowed" ).contains( word.toLowerCase() ) )
            {
                continue;
            }
            if ( IP_PATTERN.matcher( word ).find() || WEB_PATTERN.matcher( word ).find() )
            {
                return true;
            }
        }
        return false;
    }

    public boolean checkForCaps( final User user, final String message )
    {
        IConfiguration config = ConfigFiles.ANTICAPS.getConfig();

        if ( !config.getBoolean( "enabled" ) || user.hasPermission( config.getString( "bypass" ) )
                || message.length() < config.getInteger( "min-length" ) )
        {
            return false;
        }

        double upperCase = 0.0D;
        for ( int i = 0; i < message.length(); i++ )
        {
            if ( config.getString( "characters" ).contains( message.substring( i, i + 1 ) ) )
            {
                upperCase += 1.0D;
            }
        }

        return ( upperCase / message.length() ) > ( config.getDouble( "percentage" ) / 100 );
    }

    public boolean checkForSpam( final User user )
    {
        IConfiguration config = ConfigFiles.ANTISPAM.getConfig();
        if ( !config.getBoolean( "enabled" ) || user.hasPermission( config.getString( "bypass" ) ) )
        {
            return false;
        }
        if ( !user.getCooldowns().canUse( "CHATSPAM" ) )
        {
            return true;
        }
        user.getCooldowns().updateTime( "CHATSPAM",
                TimeUnit.valueOf( config.getString( "delay.unit" ).toUpperCase() ),
                config.getInteger( "delay.time" ) );
        return false;
    }

    public boolean checkForSwear( final User user, final String message )
    {
        IConfiguration config = ConfigFiles.ANTISWEAR.getConfig();
        if ( !config.getBoolean( "enabled" ) || user.hasPermission( config.getString( "bypass" ) ) )
        {
            return false;
        }

        for ( Pattern pattern : swearPatterns )
        {
            if ( pattern.matcher( message ).find() )
            {
                return true;
            }
        }

        return false;
    }

    public String replaceSwearWords( final User user, String message, final String replacement )
    {
        IConfiguration config = ConfigFiles.ANTISWEAR.getConfig();
        if ( !config.getBoolean( "enabled" ) || user.hasPermission( config.getString( "bypass" ) ) )
        {
            return message;
        }

        for ( Pattern pattern : swearPatterns )
        {
            if ( pattern.matcher( message ).find() )
            {
                message = pattern.matcher( message ).replaceAll( replacement );
            }
        }
        return message;
    }

    public String replaceSymbols( String message )
    {
        for ( Map.Entry<String, String> entry : utfSymbols.entrySet() )
        {
            String key = entry.getKey();
            String value = entry.getValue();

            if ( value.contains( ", " ) )
            {
                for ( String val : value.split( ", " ) )
                {
                    message = message.replace( val, UnicodeTranslator.translate( key ) );
                }
            }
            else
            {
                message = message.replace( value, UnicodeTranslator.translate( key ) );
            }
        }
        return message;
    }

    public String fancyFont( String message )
    {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,?;.:+=-%*$!'\"@|&<>0123456789#".toCharArray();
        StringBuilder builder = new StringBuilder();

        for ( char replaceableChar : message.toCharArray() )
        {
            int i = 0;
            boolean charFound = false;
            while ( !charFound && i < chars.length )
            {
                if ( chars[i] == replaceableChar )
                {
                    charFound = true;
                }
                i++;
            }
            if ( charFound )
            {
                builder.append( (char) ( 65248 + replaceableChar ) );
            }
            else
            {
                builder.append( replaceableChar );
            }
        }

        return builder.toString();
    }

    public void reload()
    {
        swearPatterns.clear();
        utfSymbols.getClass();

        for ( String word : ConfigFiles.ANTISWEAR.getConfig().getStringList( "words" ) )
        {
            StringBuilder builder = new StringBuilder( "\\b(" );

            for ( char o : word.toCharArray() )
            {
                builder.append( o );
                builder.append( "+(\\W|\\d\\_)*" );
            }
            builder.append( ")\\b" );

            swearPatterns.add( Pattern.compile( builder.toString() ) );
        }

        final ISection section = ConfigFiles.UTFSYMBOLS.getConfig().getSection( "symbols.symbols" );
        for ( String key : section.getKeys() )
        {
            utfSymbols.put( key, section.getString( key ) );
        }
    }
}