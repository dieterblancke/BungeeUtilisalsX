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
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishRemoveEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishRemoveEvent.PunishmentRemovalAction;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import lombok.Data;

import java.util.List;
import java.util.Optional;

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

    protected PunishmentRemovalArgs loadRemovalArguments( final User user, final List<String> args )
    {
        if ( useServerPunishments() )
        {
            if ( args.size() < 2 )
            {
                return null;
            }
        }
        else
        {
            if ( args.size() < 1 )
            {
                return null;
            }
        }
        final PunishmentRemovalArgs punishmentRemovalArgs = new PunishmentRemovalArgs();

        punishmentRemovalArgs.setExecutor( user );
        punishmentRemovalArgs.setPlayer( args.get( 0 ) );

        if ( useServerPunishments() )
        {
            punishmentRemovalArgs.setServer( args.get( 1 ) );
        }

        return punishmentRemovalArgs;
    }

    protected PunishmentArgs loadArguments( final User user, final List<String> args, final boolean withTime )
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

        punishmentArgs.setExecutor( user );
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
                punishmentArgs.setReason( Utils.formatList( args.subList( 2, args.size() ), " " ) );
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

        private User executor;
        private String player;
        private long time;
        private String server;
        private String reason;

        private UserStorage storage;

        public void setTime( final String time )
        {
            this.time = Utils.parseDateDiff( time );
        }

        public boolean hasJoined()
        {
            return dao().getUserDao().exists( player );
        }

        public UserStorage getStorage()
        {
            if ( storage == null )
            {
                return storage = dao().getUserDao().getUserData( player );
            }
            return storage;
        }

        public String getServerOrAll()
        {
            return server == null ? "ALL" : server;
        }

        public boolean launchEvent( final PunishmentType type )
        {
            final UserPunishEvent event = new UserPunishEvent(
                    type,
                    executor,
                    storage.getUuid(),
                    storage.getUserName(),
                    storage.getIp(),
                    reason,
                    useServerPunishments() ? server : "ALL",
                    time
            );
            BUCore.getApi().getEventLoader().launchEvent( event );

            if ( event.isCancelled() )
            {
                executor.sendLangMessage( "punishments.cancelled" );
                return true;
            }
            return false;
        }
    }

    @Data
    public class PunishmentRemovalArgs
    {

        private User executor;
        private String player;
        private String server;

        private UserStorage storage;

        public boolean hasJoined()
        {
            return dao().getUserDao().exists( player );
        }

        public UserStorage getStorage()
        {
            if ( storage == null )
            {
                return storage = dao().getUserDao().getUserData( player );
            }
            return storage;
        }

        public String getServerOrAll()
        {
            if ( !useServerPunishments() )
            {
                return "ALL";
            }
            return server == null ? "ALL" : server;
        }

        public void removeCachedMute()
        {
            final Optional<User> optionalUser = BUCore.getApi().getUser( player );
            if ( !optionalUser.isPresent() )
            {
                return;
            }
            final User user = optionalUser.get();
            if ( !user.getStorage().hasData( "CURRENT_MUTES" ) )
            {
                return;
            }
            final List<PunishmentInfo> mutes = user.getStorage().getData( "CURRENT_MUTES" );

            mutes.removeIf( mute ->
            {
                if ( useServerPunishments() )
                {
                    return mute.getServer().equalsIgnoreCase( this.getServerOrAll() );
                }
                else
                {
                    return true;
                }
            } );
        }

        public boolean launchEvent( final PunishmentRemovalAction type )
        {
            final UserPunishRemoveEvent event = new UserPunishRemoveEvent(
                    type,
                    executor,
                    storage.getUuid(),
                    storage.getUserName(),
                    storage.getIp(),
                    useServerPunishments() ? server : "ALL"
            );

            BUCore.getApi().getEventLoader().launchEvent( event );

            if ( event.isCancelled() )
            {
                executor.sendLangMessage( "punishments.cancelled" );
                return true;
            }
            return false;
        }
    }
}
