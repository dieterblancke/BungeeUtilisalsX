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

package com.dbsoftwares.bungeeutilisals.api;

import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.CommandSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BUCore
{

    private static BUAPI instance = null;
    private static Logger logger = LoggerFactory.getLogger( "BungeeUtilisalsX" );

    private BUCore()
    {
        throw new UnsupportedOperationException( "This class cannot be instantiated." );
    }

    /**
     * Returns an instance of the {@link BUAPI},
     *
     * @return The BungeeUtilisals API.
     */
    public static BUAPI getApi()
    {
        return instance;
    }

    private static void initAPI( final BUAPI inst )
    {
        instance = inst;
    }

    public static void sendMessage( final CommandSender sender, final String message )
    {
        IConfiguration config = getApi().getLanguageManager().getLanguageConfiguration( getApi().getPlugin().getDescription().getName(), sender );

        sender.sendMessage( Utils.format( config.getString( "prefix" ), message ) );
    }

    public static Logger getLogger()
    {
        return logger;
    }

    public static void logException( final Exception e )
    {
        logger.error( "An error occured: ", e );
    }
}