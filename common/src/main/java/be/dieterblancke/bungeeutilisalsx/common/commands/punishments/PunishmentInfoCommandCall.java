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
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

import static be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao.useServerPunishments;

public class PunishmentInfoCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( useServerPunishments() )
        {
            if ( args.size() < 2 )
            {
                user.sendLangMessage( "punishments.punishmentinfo.usage-server" );
                return;
            }
        }
        else
        {
            if ( args.size() < 1 )
            {
                user.sendLangMessage( "punishments.punishmentinfo.usage" );
                return;
            }
        }

        final Dao dao = BuX.getApi().getStorageManager().getDao();
        final String username = args.get( 0 );
        final String server = useServerPunishments() ? args.get( 1 ) : null;

        if ( !dao.getUserDao().exists( username ) )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }
        final UserStorage storage = dao.getUserDao().getUserData( username );
        final String action;

        if ( useServerPunishments() )
        {
            action = args.size() > 2 ? args.get( 2 ) : "all";
        }
        else
        {
            action = args.size() > 1 ? args.get( 1 ) : "all";
        }

        if ( action.equalsIgnoreCase( "all" ) )
        {
            for ( PunishmentType type : PunishmentType.values() )
            {
                if ( type.equals( PunishmentType.KICK ) || type.equals( PunishmentType.WARN ) )
                {
                    continue;
                }
                sendTypeInfo( user, storage, server, type );
            }
        }
        else
        {
            for ( String typeStr : action.split( "," ) )
            {
                final PunishmentType type = Utils.valueOfOr( typeStr.toUpperCase(), PunishmentType.BAN );
                if ( type.equals( PunishmentType.KICK ) || type.equals( PunishmentType.WARN ) )
                {
                    continue;
                }
                sendTypeInfo( user, storage, server, type );
            }
        }
    }

    private void sendTypeInfo( final User user, final UserStorage storage, String server, final PunishmentType type )
    {
        if ( !useServerPunishments() || server == null )
        {
            server = "ALL";
        }
        final PunishmentDao dao = BuX.getApi().getStorageManager().getDao().getPunishmentDao();

        switch ( type )
        {
            case BAN:
            case TEMPBAN:
                if ( dao.getBansDao().isBanned( storage.getUuid(), server ) )
                {
                    sendInfoMessage( user, storage, type, true, dao.getBansDao().getCurrentBan( storage.getUuid(), server ) );
                }
                else
                {
                    sendInfoMessage( user, storage, type, false, null );
                }
                break;
            case IPBAN:
            case IPTEMPBAN:
                if ( dao.getBansDao().isIPBanned( storage.getIp(), server ) )
                {
                    sendInfoMessage( user, storage, type, true, dao.getBansDao().getCurrentIPBan( storage.getIp(), server ) );
                }
                else
                {
                    sendInfoMessage( user, storage, type, false, null );
                }
                break;
            case MUTE:
            case TEMPMUTE:
                if ( dao.getMutesDao().isMuted( storage.getUuid(), server ) )
                {
                    sendInfoMessage( user, storage, type, true, dao.getMutesDao().getCurrentMute( storage.getUuid(), server ) );
                }
                else
                {
                    sendInfoMessage( user, storage, type, false, null );
                }
                break;
            case IPMUTE:
            case IPTEMPMUTE:
                if ( dao.getMutesDao().isIPMuted( storage.getIp(), server ) )
                {
                    sendInfoMessage( user, storage, type, true, dao.getMutesDao().getCurrentIPMute( storage.getIp(), server ) );
                }
                else
                {
                    sendInfoMessage( user, storage, type, false, null );
                }
                break;
            case KICK:
            case WARN:
                break;
        }
    }

    private void sendInfoMessage( final User user, final UserStorage storage, final PunishmentType type,
                                  final boolean punished, final PunishmentInfo info )
    {
        if ( punished )
        {
            user.sendLangMessage(
                    "punishments.punishmentinfo.typeinfo.found",
                    BuX.getApi().getPunishmentExecutor().getPlaceHolders( info ).toArray( new Object[0] )
            );
        }
        else
        {
            user.sendLangMessage(
                    "punishments.punishmentinfo.typeinfo.notfound",
                    "{user}", storage.getUserName(),
                    "{type}", type.toString().toLowerCase()
            );
        }
    }
}
