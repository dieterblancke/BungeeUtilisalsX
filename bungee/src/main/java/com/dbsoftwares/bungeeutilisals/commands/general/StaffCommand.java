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

package com.dbsoftwares.bungeeutilisals.commands.general;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.data.StaffRankData;
import com.dbsoftwares.bungeeutilisals.api.data.StaffUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MessageBuilder;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;
import java.util.stream.Collectors;

public class StaffCommand extends BUCommand
{

    public StaffCommand()
    {
        super(
                "staff",
                Arrays.asList( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "staff.aliases" ).split( ", " ) ),
                ConfigFiles.GENERALCOMMANDS.getConfig().getString( "staff.permission" )
        );
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        return ImmutableList.of();
    }

    @Override
    public void onExecute( User user, String[] args )
    {
        final List<StaffUser> staffUsers = BungeeUtilisals.getInstance().getStaffMembers();
        if ( staffUsers.isEmpty() )
        {
            user.sendLangMessage( "general-commands.staff.no_staff" );
            return;
        }

        final Map<StaffRankData, List<StaffUser>> staffMembers = staffUsers
                .stream()
                .collect( Collectors.groupingBy( StaffUser::getRank ) );

        final LinkedList<StaffRankData> onlineStaffRanks = staffMembers
                .keySet()
                .stream()
                .sorted( Comparator.comparingInt( StaffRankData::getPriority ) )
                .collect( Collectors.toCollection( Lists::newLinkedList ) );

        user.sendLangMessage( "general-commands.staff.head", "{total}", staffUsers.size() );

        onlineStaffRanks.forEach( rank ->
        {
            final List<StaffUser> users = staffMembers.get( rank );

            final TextComponent component = MessageBuilder.buildMessage(
                    user,
                    user.getLanguageConfig().getSection( "general-commands.staff.rank" ),
                    "{rank_displayname}", Utils.c( rank.getDisplay() ),
                    "{amount_online}", users.size(),
                    "{total}", staffUsers.size()
            );

            findUsersComponents( component ).forEach( c ->
            {
                c.setText( "" );

                users.sort( Comparator.comparing( StaffUser::getName ) );
                final Iterator<StaffUser> userIt = users.iterator();

                while ( userIt.hasNext() )
                {
                    final StaffUser u = userIt.next();
                    final ServerInfo info = BUCore.getApi().getPlayerUtils().findPlayer( u.getName() );

                    c.addExtra( MessageBuilder.buildMessage(
                            user,
                            user.getLanguageConfig().getSection( "general-commands.staff.users.user" ),
                            "{username}", u.getName(),
                            "{server}", info == null ? "Unknown" : info.getName()
                    ) );

                    if ( userIt.hasNext() )
                    {
                        c.addExtra( user.buildLangMessage( "general-commands.staff.users.separator" ) );
                    }
                }
            } );

            user.sendMessage( component );
        } );

        user.sendLangMessage( "general-commands.staff.foot", "{total}", staffUsers.size() );
    }

    private List<TextComponent> findUsersComponents( final TextComponent component )
    {
        final List<TextComponent> components = component.getExtra().stream()
                .filter( c -> c instanceof TextComponent )
                .map( c -> (TextComponent) c )
                .collect( Collectors.toList() );
        final List<TextComponent> finalComponents = Lists.newArrayList();

        for ( TextComponent c : components )
        {
            if ( c.getExtra() != null && !c.getExtra().isEmpty() )
            {
                finalComponents.addAll( findUsersComponents( c ) );
            }
            if ( c.getText().contains( "{users}" ) )
            {
                finalComponents.add( c );
            }
        }
        return finalComponents;
    }
}
