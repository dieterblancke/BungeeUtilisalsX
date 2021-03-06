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

package com.dbsoftwares.bungeeutilisals.commands.general.domains;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.UserUtils;

import java.util.List;
import java.util.Map;

public class DomainsSearchSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( User user, List<String> args, List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "general-commands.domains.search.usage" );
            return;
        }
        final String domainToSearch = args.get( 0 );
        final Map<String, Integer> domains = BUCore.getApi().getStorageManager().getDao().getUserDao().searchJoinedHosts( domainToSearch );

        user.sendLangMessage( "general-commands.domains.search.header", "{total}", domains.size() );

        domains.entrySet().stream()
                .sorted( ( o1, o2 ) -> Integer.compare( o2.getValue(), o1.getValue() ) )
                .forEach( entry ->
                        user.sendLangMessage(
                                "general-commands.domains.search.format",
                                "{domain}", entry.getKey(),
                                "{online}", UserUtils.getOnlinePlayersOnDomain( entry.getKey() ),
                                "{total}", entry.getValue()
                        )
                );

        user.sendLangMessage( "general-commands.domains.search.footer", "{total}", domains.size() );
    }
}
