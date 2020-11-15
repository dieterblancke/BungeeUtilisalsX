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
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisalsx.common.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.utils.MathUtils;
import com.dbsoftwares.bungeeutilisalsx.common.utils.Utils;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PunishmentHistoryCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "punishments.punishmenthistory.usage" );
            return;
        }

        final Dao dao = BUCore.getApi().getStorageManager().getDao();
        final String username = args.get( 0 );

        if ( !dao.getUserDao().exists( username ) )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }
        final UserStorage storage = dao.getUserDao().getUserData( username );
        final String action = args.size() > 1 ? args.get( 1 ) : "all";
        int page = args.size() > 2
                ? ( MathUtils.isInteger( args.get( 2 ) ) ? Integer.parseInt( args.get( 2 ) ) : 1 )
                : 1;

        final List<PunishmentInfo> allPunishments = listPunishments( storage, action );
        if ( allPunishments.isEmpty() )
        {
            user.sendLangMessage(
                    "punishments.punishmenthistory.no-punishments",
                    "{user}", username,
                    "{type}", action
            );
            return;
        }
        final int pages = (int) Math.ceil( (double) allPunishments.size() / 10 );

        if ( page > pages )
        {
            page = pages;
        }

        final int previous = page > 1 ? page - 1 : 1;
        final int next = Math.min( page + 1, pages );

        int maxNumber = page * 10;
        int minNumber = maxNumber - 10;

        if ( maxNumber > allPunishments.size() )
        {
            maxNumber = allPunishments.size();
        }

        final List<PunishmentInfo> punishments = allPunishments.subList( minNumber, maxNumber );
        user.sendLangMessage(
                "punishments.punishmenthistory.head",
                "{previousPage}", previous,
                "{currentPage}", page,
                "{nextPage}", next,
                "{maxPages}", pages,
                "{type}", action
        );

        punishments.forEach( punishment ->
                user.sendLangMessage(
                        "punishments.punishmenthistory.format",
                        BUCore.getApi().getPunishmentExecutor().getPlaceHolders( punishment ).toArray( new Object[0] )
                )
        );
        user.sendLangMessage(
                "punishments.punishmenthistory.foot",
                "{punishmentAmount}", allPunishments.size(),
                "{user}", username,
                "{type}", action
        );
    }

    private List<PunishmentInfo> listPunishments( final UserStorage storage, final String action )
    {
        final List<PunishmentInfo> list = Lists.newArrayList();

        if ( action.equalsIgnoreCase( "all" ) )
        {
            for ( PunishmentType type : PunishmentType.values() )
            {
                list.addAll( listPunishments( storage, type ) );
            }
        }
        else
        {
            for ( String typeStr : action.split( "," ) )
            {
                final PunishmentType type = Utils.valueOfOr( typeStr.toUpperCase(), PunishmentType.BAN );

                list.addAll( listPunishments( storage, type ) );
            }
        }
        return list;
    }

    private List<PunishmentInfo> listPunishments( final UserStorage storage, final PunishmentType type )
    {
        final Predicate<PunishmentInfo> permanentFilter = punishment -> !punishment.isTemporary();
        final Predicate<PunishmentInfo> temporaryFilter = PunishmentInfo::isTemporary;

        switch ( type )
        {
            default:
            case BAN:
                return BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getBans( storage.getUuid() )
                        .stream().filter( permanentFilter ).collect( Collectors.toList() );
            case TEMPBAN:
                return BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getBans( storage.getUuid() )
                        .stream().filter( temporaryFilter ).collect( Collectors.toList() );
            case IPBAN:
                return BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getIPBans( storage.getIp() )
                        .stream().filter( permanentFilter ).collect( Collectors.toList() );
            case IPTEMPBAN:
                return BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getIPBans( storage.getIp() )
                        .stream().filter( temporaryFilter ).collect( Collectors.toList() );
            case MUTE:
                return BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getMutes( storage.getUuid() )
                        .stream().filter( permanentFilter ).collect( Collectors.toList() );
            case TEMPMUTE:
                return BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getMutes( storage.getUuid() )
                        .stream().filter( temporaryFilter ).collect( Collectors.toList() );
            case IPMUTE:
                return BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getIPMutes( storage.getIp() )
                        .stream().filter( permanentFilter ).collect( Collectors.toList() );
            case IPTEMPMUTE:
                return BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getIPMutes( storage.getIp() )
                        .stream().filter( temporaryFilter ).collect( Collectors.toList() );
            case KICK:
                return BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().getKicks( storage.getUuid() );
            case WARN:
                return BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().getWarns( storage.getUuid() );
        }
    }
}
