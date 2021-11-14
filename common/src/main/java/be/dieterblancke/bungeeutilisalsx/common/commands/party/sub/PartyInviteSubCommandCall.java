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
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.UserUtils;

import java.util.List;
import java.util.Optional;

public class PartyInviteSubCommandCall implements CommandCall
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

        if ( args.size() != 1 )
        {
            user.sendLangMessage( "party.invite.usage" );
            return;
        }
        if ( !party.getOwner().getUuid().equals( user.getUuid() ) )
        {
            user.sendLangMessage( "party.invite.not-allowed" );
            return;
        }
        final String targetUser = args.get( 0 );

        UserUtils.getUserStorage( targetUser, user::sendLangMessage ).ifPresent( target ->
        {
            if ( target.getIgnoredUsers().stream().anyMatch( ignored -> ignored.equalsIgnoreCase( user.getName() ) ) )
            {
                user.sendLangMessage( "party.invite.ignored" );
                return;
            }

            // TODO: add invite to party (and add invite, and join request methods to PartyManager) and message the invited user
            // TODO: use invite setting from party config
        } );
    }

    @Override
    public String getDescription()
    {
        return "Invites someone to your party.";
    }

    @Override
    public String getUsage()
    {
        return "/party invite (user)";
    }
}
