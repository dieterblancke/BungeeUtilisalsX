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
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.data.StaffRankData;
import com.dbsoftwares.bungeeutilisals.api.data.StaffUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MessageBuilder;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;
import java.util.stream.Collectors;

public class StaffCommandCall implements CommandCall
{

    @Override
    public void onExecute( User user, List<String> args, List<String> parameters )
    {
        if ( args.size() > 0 )
        {
            final IConfiguration config = ConfigFiles.GENERALCOMMANDS.getConfig();

            // Big yikes code, but works for now
            final List<String> commands = Arrays.asList( config.getString( "staff.toggle.aliases" ).split( ", " ) );

            if ( config.getBoolean( "staff.toggle.enabled" )
                    && ( config.getString( "staff.toggle.name" ).equalsIgnoreCase( args.get( 0 ) ) || commands.contains( args.get( 0 ) ) )
                    && user.hasPermission( config.getString( "staff.toggle.permission" ) ) )
            {
                StaffUser staffUser = null;

                for ( StaffUser su : BungeeUtilisals.getInstance().getStaffMembers() )
                {
                    if ( su.getUuid().equals( user.getUuid() ) )
                    {
                        staffUser = su;
                    }
                }

                if ( staffUser != null )
                {
                    if ( staffUser.isHidden() )
                    {
                        staffUser.setHidden( false );
                        user.sendLangMessage( "general-commands.staff.toggle.unhidden" );
                    }
                    else
                    {
                        staffUser.setHidden( true );
                        user.sendLangMessage( "general-commands.staff.toggle.hidden" );
                    }
                    return;
                }
            }
        }

        final List<StaffUser> staffUsers = BungeeUtilisals.getInstance().getStaffMembers()
                .stream()
                .filter( staffUser -> !staffUser.isHidden() )
                .collect( Collectors.toList() );
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

        for ( StaffRankData rank : onlineStaffRanks )
        {
            final List<StaffUser> users = staffMembers.get( rank );
            final TextComponent component = MessageBuilder.buildMessage(
                    user,
                    user.getLanguageConfig().getSection( "general-commands.staff.rank" ),
                    "{rank_displayname}", Utils.c( rank.getDisplay() ),
                    "{amount_online}", users.size(),
                    "{total}", staffUsers.size()
            );

            final List<TextComponent> components = findUsersComponents( component );

            for ( TextComponent c : components )
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
            }

            user.sendMessage( component );
        }

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
