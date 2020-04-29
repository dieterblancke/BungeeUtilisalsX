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
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaffHistoryCommand implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "punishments.staffhistory.usage" );
            return;
        }

        final String username = args.get( 0 );
        final String action = args.size() > 1 ? args.get( 1 ) : "all";
        int page = args.size() > 2
                ? ( MathUtils.isInteger( args.get( 2 ) ) ? Integer.parseInt( args.get( 2 ) ) : 1 )
                : 1;

        final List<PunishmentInfo> allPunishments = listPunishments( username, action );
        if ( allPunishments.isEmpty() )
        {
            user.sendLangMessage(
                    "punishments.staffhistory.no-punishments",
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
                "punishments.staffhistory.head",
                "{previousPage}", previous,
                "{currentPage}", page,
                "{nextPage}", next,
                "{maxPages}", pages,
                "{type}", action
        );

        punishments.forEach( punishment ->
                user.sendLangMessage(
                        "punishments.staffhistory.format",
                        BUCore.getApi().getPunishmentExecutor().getPlaceHolders( punishment ).toArray( new Object[0] )
                )
        );
        user.sendLangMessage(
                "punishments.staffhistory.foot",
                "{punishmentAmount}", allPunishments.size(),
                "{user}", username,
                "{type}", action
        );
    }

    private List<PunishmentInfo> listPunishments( final String name, final String action )
    {
        final List<PunishmentInfo> list = Lists.newArrayList();

        if ( action.equalsIgnoreCase( "all" ) )
        {
            return listAllPunishments( name );
        }
        else
        {
            for ( String typeStr : action.split( "," ) )
            {
                final PunishmentType type = Utils.valueOfOr( PunishmentType.class, typeStr.toUpperCase(), null );

                if ( type != null )
                {
                    list.addAll( listPunishments( name, type ) );
                }
            }
        }
        return list;
    }

    private List<PunishmentInfo> listAllPunishments( final String name )
    {
        final PunishmentDao punishmentDao = BUCore.getApi().getStorageManager().getDao().getPunishmentDao();
        final List<PunishmentInfo> punishments = Lists.newArrayList();

        punishments.addAll( punishmentDao.getBansDao().getBansExecutedBy( name ) );
        punishments.addAll( punishmentDao.getMutesDao().getMutesExecutedBy( name ) );
        punishments.addAll( punishmentDao.getKickAndWarnDao().getWarnsExecutedBy( name ) );
        punishments.addAll( punishmentDao.getKickAndWarnDao().getKicksExecutedBy( name ) );

        return punishments;
    }

    private List<PunishmentInfo> listPunishments( final String name, final PunishmentType type )
    {
        final Predicate<PunishmentInfo> permanentFilter = punishment -> !punishment.isTemporary();
        final Predicate<PunishmentInfo> temporaryFilter = PunishmentInfo::isTemporary;
        final Predicate<PunishmentInfo> ipFilter = punishment -> punishment.toString().contains( "IP" );
        final Predicate<PunishmentInfo> notIpFilter = punishment -> !punishment.toString().contains( "IP" );

        final PunishmentDao punishmentDao = BUCore.getApi().getStorageManager().getDao().getPunishmentDao();

        Stream<PunishmentInfo> stream;
        if ( type.toString().contains( "BAN" ) )
        {
            stream = punishmentDao.getBansDao().getBansExecutedBy( name ).stream();
        }
        else if ( type.toString().contains( "MUTE" ) )
        {
            stream = punishmentDao.getMutesDao().getMutesExecutedBy( name ).stream();
        }
        else if ( type == PunishmentType.KICK )
        {
            stream = punishmentDao.getKickAndWarnDao().getKicksExecutedBy( name ).stream();
        }
        else
        {
            stream = punishmentDao.getKickAndWarnDao().getWarnsExecutedBy( name ).stream();
        }

        if ( type.isActivatable() )
        {
            stream = type.isTemporary()
                    ? stream.filter( temporaryFilter )
                    : stream.filter( permanentFilter );
            stream = type.isIP()
                    ? stream.filter( ipFilter )
                    : stream.filter( notIpFilter );
        }

        return stream.collect( Collectors.toList() );
    }
}
