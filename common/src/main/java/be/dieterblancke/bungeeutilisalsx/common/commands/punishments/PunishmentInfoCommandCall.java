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
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.PunishmentDao.useServerPunishments;

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

        dao.getUserDao().getUserData( username ).thenAccept( storage ->
        {
            if ( !storage.isLoaded() )
            {
                user.sendLangMessage( "never-joined" );
                return;
            }

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
        } );
    }

    @Override
    public String getDescription()
    {
        return "Shows you the current status for a specific punishment type or all punishment types for a specific user.";
    }

    @Override
    public String getUsage()
    {
        return "/punishmentinfo (user) <server> [type / all]";
    }

    private void sendTypeInfo( final User user, final UserStorage storage, String server, final PunishmentType type )
    {
        if ( !useServerPunishments() || server == null )
        {
            server = "ALL";
        }
        final PunishmentDao dao = BuX.getApi().getStorageManager().getDao().getPunishmentDao();

        if ( !type.isEnabled() )
        {
            return;
        }

        final CompletableFuture<PunishmentInfo> task;
        switch ( type )
        {
            case BAN:
            case TEMPBAN:
                task = dao.getBansDao().getCurrentBan( storage.getUuid(), server );
                break;
            case IPBAN:
            case IPTEMPBAN:
                task = dao.getBansDao().getCurrentIPBan( storage.getIp(), server );
                break;
            case MUTE:
            case TEMPMUTE:
                task = dao.getMutesDao().getCurrentMute( storage.getUuid(), server );
                break;
            case IPMUTE:
            case IPTEMPMUTE:
                task = dao.getMutesDao().getCurrentIPMute( storage.getIp(), server );
                break;
            default:
                task = null;
                break;
        }

        if ( task != null )
        {
            task.thenAccept( info -> sendInfoMessage( user, storage, type, info ) );
        }
    }

    private void sendInfoMessage( final User user,
                                  final UserStorage storage,
                                  final PunishmentType type,
                                  final PunishmentInfo info )
    {
        if ( info != null )
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
