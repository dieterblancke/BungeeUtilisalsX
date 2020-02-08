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

package com.dbsoftwares.bungeeutilisals.commands.punishments;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import lombok.Data;

import java.util.List;

public abstract class PunishmentCommand implements CommandCall
{

    // Utility methods
    protected boolean useServerPunishments()
    {
        return FileLocation.PUNISHMENTS.getConfiguration().getBoolean( "per-server-punishments" );
    }

    protected Dao dao()
    {
        return BUCore.getApi().getStorageManager().getDao();
    }

    protected PunishmentArgs loadArguments( final List<String> args, final boolean withTime )
    {
        if ( withTime )
        {
            if ( useServerPunishments() )
            {
                if ( args.size() < 4 )
                {
                    return null;
                }
            }
            if ( args.size() < 3 )
            {
                return null;
            }
        }
        else
        {
            if ( useServerPunishments() )
            {
                if ( args.size() < 3 )
                {
                    return null;
                }
            }
            if ( args.size() < 2 )
            {
                return null;
            }
        }
        final PunishmentArgs punishmentArgs = new PunishmentArgs();

        punishmentArgs.setPlayer( args.get( 0 ) );

        if ( withTime )
        {
            if ( useServerPunishments() )
            {
                punishmentArgs.setTime( args.get( 1 ) );
                punishmentArgs.setServer( args.get( 2 ) );
                punishmentArgs.setReason( Utils.formatList( args.subList( 3, args.size() ), " " ) );
            }
            else
            {
                punishmentArgs.setTime( args.get( 1 ) );
                punishmentArgs.setReason( Utils.formatList( args.subList( 1, args.size() ), " " ) );
            }
        }
        else
        {
            if ( useServerPunishments() )
            {
                punishmentArgs.setServer( args.get( 1 ) );
                punishmentArgs.setReason( Utils.formatList( args.subList( 2, args.size() ), " " ) );
            }
            else
            {
                punishmentArgs.setReason( Utils.formatList( args.subList( 1, args.size() ), " " ) );
            }
        }

        return punishmentArgs;
    }

    @Data
    public class PunishmentArgs
    {
        private String player;
        private long time;
        private String server;
        private String reason;

        public void setTime( final String time )
        {
            this.time = Utils.parseDateDiff( time );
        }

        public boolean hasJoined()
        {
            return dao().getUserDao().exists( player );
        }
    }
}
