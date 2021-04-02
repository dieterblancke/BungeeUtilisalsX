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

package be.dieterblancke.bungeeutilisalsx.common.commands.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.BridgeType;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishRemoveEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishRemoveEvent.PunishmentRemovalAction;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.bridge.types.UserAction;
import be.dieterblancke.bungeeutilisalsx.common.bridge.types.UserActionType;
import be.dieterblancke.bungeeutilisalsx.common.bridge.util.BridgedUserMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class PunishmentCommand implements CommandCall
{

    // Utility methods
    protected boolean useServerPunishments()
    {
        return ConfigFiles.PUNISHMENT_CONFIG.getConfig().getBoolean( "per-server-punishments" );
    }

    protected Dao dao()
    {
        return BuX.getApi().getStorageManager().getDao();
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

    protected void attemptKick( final UserStorage storage, final String path, final PunishmentInfo info )
    {
        final Optional<User> optionalTarget = BuX.getApi().getUser( storage.getUserName() );

        if ( optionalTarget.isPresent() )
        {
            final User target = optionalTarget.get();

            if ( info.getType().isIP() )
            {
                BuX.getApi().getUsers().stream()
                        .filter( u -> u.getIp().equalsIgnoreCase( storage.getIp() ) )
                        .forEach( u -> kickUser( u, path, info ) );

                bridgedKick( storage, path, info );
            }
            else
            {
                kickUser( target, path, info );
            }
        }
        else
        {
            bridgedKick( storage, path, info );
        }
    }

    private void kickUser( final User user, final String path, final PunishmentInfo info )
    {
        String kick = null;
        if ( BuX.getApi().getPunishmentExecutor().isTemplateReason( info.getReason() ) )
        {
            kick = Utils.formatList( BuX.getApi().getPunishmentExecutor().searchTemplate(
                    user.getLanguageConfig(), info.getType(), info.getReason()
            ), "\n" );
        }
        if ( kick == null )
        {
            kick = Utils.formatList(
                    user.getLanguageConfig().getStringList( path ),
                    "\n"
            );
        }
        kick = BuX.getApi().getPunishmentExecutor().setPlaceHolders( kick, info );
        user.kick( kick );
    }

    private void bridgedKick( final UserStorage storage, final String path, final PunishmentInfo info )
    {
        if ( BuX.getApi().getBridgeManager().useBridging() )
        {
            final Map<String, Object> data = Maps.newHashMap();
            data.put( "reason", info.getReason() );
            data.put( "type", info.getType() );

            BuX.getApi().getBridgeManager().getBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Lists.newArrayList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
                    "USER",
                    new UserAction(
                            storage,
                            info.getType().isIP() ? UserActionType.KICK_IP : UserActionType.KICK,
                            new BridgedUserMessage(
                                    true,
                                    path,
                                    data,
                                    BuX.getApi().getPunishmentExecutor().getPlaceHolders( info ).toArray()
                            )
                    )
            );
        }
    }

    protected void attemptMute( final UserStorage storage, final String path, final PunishmentInfo info )
    {
        final Optional<User> optionalTarget = BuX.getApi().getUser( storage.getUserName() );

        if ( optionalTarget.isPresent() )
        {
            final User target = optionalTarget.get();

            if ( info.getType().isIP() )
            {
                BuX.getApi().getUsers().stream()
                        .filter( u -> u.getIp().equalsIgnoreCase( storage.getIp() ) )
                        .forEach( u -> muteUser( u, path, info ) );

                bridgedMute( storage, path, info );
            }
            else
            {
                muteUser( target, path, info );
            }
        }
        else
        {
            bridgedMute( storage, path, info );
        }
    }

    private void muteUser( final User user, final String path, final PunishmentInfo info )
    {
        List<String> mute = null;
        if ( BuX.getApi().getPunishmentExecutor().isTemplateReason( info.getReason() ) )
        {
            mute = BuX.getApi().getPunishmentExecutor().searchTemplate(
                    user.getLanguageConfig(), info.getType(), info.getReason()
            );
        }
        if ( mute == null )
        {
            user.sendLangMessage( "punishments.mute.onmute", BuX.getApi().getPunishmentExecutor().getPlaceHolders( info ).toArray() );
        }
        else
        {
            mute.forEach( str -> user.sendRawColorMessage( BuX.getApi().getPunishmentExecutor().setPlaceHolders( str, info ) ) );
        }
    }

    private void bridgedMute( final UserStorage storage, final String path, final PunishmentInfo info )
    {
        if ( BuX.getApi().getBridgeManager().useBridging() )
        {
            final Map<String, Object> data = Maps.newHashMap();
            data.put( "reason", info.getReason() );
            data.put( "type", info.getType() );

            BuX.getApi().getBridgeManager().getBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Lists.newArrayList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
                    "USER",
                    new UserAction(
                            storage,
                            info.getType().isIP() ? UserActionType.MUTE_IP : UserActionType.MUTE,
                            new BridgedUserMessage(
                                    true,
                                    path,
                                    data,
                                    BuX.getApi().getPunishmentExecutor().getPlaceHolders( info ).toArray()
                            )
                    )
            );
        }
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
            BuX.getApi().getEventLoader().launchEvent( event );

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
            final Optional<User> optionalUser = BuX.getApi().getUser( player );
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

            BuX.getApi().getEventLoader().launchEvent( event );

            if ( event.isCancelled() )
            {
                executor.sendLangMessage( "punishments.cancelled" );
                return true;
            }
            return false;
        }
    }
}
