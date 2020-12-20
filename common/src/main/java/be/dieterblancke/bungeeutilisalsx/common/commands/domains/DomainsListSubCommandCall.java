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

package be.dieterblancke.bungeeutilisalsx.common.commands.domains;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.UserUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;
import java.util.Map;

public class DomainsListSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( User user, List<String> args, List<String> parameters )
    {
        final Map<String, Integer> domains = BuX.getApi().getStorageManager().getDao().getUserDao().getJoinedHostList();

        user.sendLangMessage( "general-commands.domains.list.header", "{total}", domains.size() );

        domains.entrySet().stream()
                .sorted( ( o1, o2 ) -> Integer.compare( o2.getValue(), o1.getValue() ) )
                .forEach( entry ->
                        user.sendLangMessage(
                                "general-commands.domains.list.format",
                                "{domain}", entry.getKey(),
                                "{online}", UserUtils.getOnlinePlayersOnDomain( entry.getKey() ),
                                "{total}", entry.getValue()
                        )
                );

        user.sendLangMessage( "general-commands.domains.list.footer", "{total}", domains.size() );
    }
}
