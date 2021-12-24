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

package be.dieterblancke.bungeeutilisalsx.common.commands.party.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PartyInfoSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final Optional<Party> optionalParty = BuX.getInstance().getPartyManager().getCurrentPartyFor( user.getName() );

        if ( !optionalParty.isPresent() )
        {
            user.sendLangMessage( "party.not-in-party" );
            return;
        }
        final Party party = optionalParty.get();

        user.sendLangMessage(
                "party.info",
                "{party-owner}", party.getOwner().getNickName(),
                "{party-created-at}", Utils.formatDate( party.getCreatedAt(), user.getLanguageConfig().getConfig() ),
                "{party-member-count}", party.getPartyMembers().size(),
                "{party-member-list}", party.getPartyMembers().stream().map( PartyMember::getNickName ).collect( Collectors.joining( ", " ) ),
                "{party-invitation-count}", party.getSentInvites().size(),
                "{party-joinrequest-count}", party.getJoinRequests().size()
        );
    }

    @Override
    public String getDescription()
    {
        return "Shows basic information about your current party.";
    }

    @Override
    public String getUsage()
    {
        return "/party info";
    }
}
