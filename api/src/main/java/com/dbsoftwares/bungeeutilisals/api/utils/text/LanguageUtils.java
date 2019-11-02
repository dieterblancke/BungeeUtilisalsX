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

package com.dbsoftwares.bungeeutilisals.api.utils.text;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LanguageUtils
{

    public static void sendLangMessage( final ProxiedPlayer player, final String path )
    {
        sendLangMessage( (CommandSender) player, path );
    }

    public static void sendLangMessage( final ProxiedPlayer player, final String path, final Object... placeholders )
    {
        sendLangMessage( (CommandSender) player, path, placeholders );
    }

    public static void sendLangMessage( final CommandSender sender, final String path )
    {
        sendLangMessage( BUCore.getApi().getLanguageManager(), BUCore.getApi().getPlugin().getDescription().getName(), sender, path );
    }

    public static void sendLangMessage( final CommandSender sender, final String path, final Object... placeholders )
    {
        sendLangMessage( BUCore.getApi().getLanguageManager(), BUCore.getApi().getPlugin().getDescription().getName(), sender, path, placeholders );
    }

    public static void sendLangMessage( final ILanguageManager languageManager, final String plugin, final CommandSender sender, final String path )
    {
        final IConfiguration config = languageManager.getLanguageConfiguration( plugin, sender );

        if ( !config.exists( path ) )
        {
            return;
        }

        if ( config.isList( path ) )
        {
            for ( String message : config.getStringList( path ) )
            {
                sender.sendMessage( Utils.format( message ) );
            }
        } else
        {
            if ( config.getString( path ).isEmpty() )
            {
                return;
            }
            sender.sendMessage( Utils.format( config.getString( path ) ) );
        }
    }

    public static void sendLangMessage( final ILanguageManager languageManager, final String plugin, final CommandSender sender, final String path, final Object... placeholders )
    {
        final IConfiguration config = languageManager.getLanguageConfiguration( plugin, sender );

        if ( !config.exists( path ) )
        {
            return;
        }

        if ( config.isList( path ) )
        {
            for ( String message : config.getStringList( path ) )
            {
                message = replacePlaceHolders( null, message, placeholders );

                sender.sendMessage( Utils.format( message ) );
            }
        } else
        {
            if ( config.getString( path ).isEmpty() )
            {
                return;
            }
            String message = replacePlaceHolders( null, config.getString( path ), placeholders );

            sender.sendMessage( Utils.format( message ) );
        }
    }

    public static void sendLangMessage( final ILanguageManager languageManager, final String plugin, final User user, final String path )
    {
        final IConfiguration config = languageManager.getLanguageConfiguration( plugin, user );

        if ( !config.exists( path ) )
        {
            return;
        }

        if ( config.isList( path ) )
        {
            for ( String message : config.getStringList( path ) )
            {
                user.sendMessage( Utils.format( message ) );
            }
        } else
        {
            if ( config.getString( path ).isEmpty() )
            {
                return;
            }
            user.sendMessage( Utils.format( config.getString( path ) ) );
        }
    }

    public static void sendLangMessage( final ILanguageManager languageManager, final String plugin, final User user, final String path, final Object... placeholders )
    {
        final IConfiguration config = languageManager.getLanguageConfiguration( plugin, user );

        if ( !config.exists( path ) )
        {
            return;
        }

        if ( config.isList( path ) )
        {
            for ( String message : config.getStringList( path ) )
            {
                message = replacePlaceHolders( user, message, placeholders );
                user.sendMessage( Utils.format( message ) );
            }
        } else
        {
            if ( config.getString( path ).isEmpty() )
            {
                return;
            }
            String message = replacePlaceHolders( user, config.getString( path ), placeholders );
            user.sendMessage( Utils.format( message ) );
        }
    }

    private static String replacePlaceHolders( User user, String message, Object... placeholders )
    {
        for ( int i = 0; i < placeholders.length - 1; i += 2 )
        {
            message = message.replace( placeholders[i].toString(), placeholders[i + 1].toString() );
        }
        message = PlaceHolderAPI.formatMessage( user, message );
        return message;
    }
}
