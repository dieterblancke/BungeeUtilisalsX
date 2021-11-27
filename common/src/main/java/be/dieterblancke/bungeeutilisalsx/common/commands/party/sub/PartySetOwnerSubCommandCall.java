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
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.UserUtils;

import java.util.List;
import java.util.Optional;

public class PartySetOwnerSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() != 1 )
        {
            user.sendLangMessage( "party.setowner.usage" );
            return;
        }
        final Optional<Party> optionalParty = BuX.getInstance().getPartyManager().getCurrentPartyFor( user.getName() );

        if ( !optionalParty.isPresent() )
        {
            user.sendLangMessage( "party.not-in-party" );
            return;
        }
        final Party party = optionalParty.get();

        if ( !party.getOwner().getUuid().equals( user.getUuid() ) )
        {
            user.sendLangMessage( "party.setowner.not-allowed" );
            return;
        }

        final String newOwner = args.get( 0 );
        final Optional<UserStorage> userStorage = UserUtils.getUserStorage( newOwner, user::sendLangMessage );

        userStorage.ifPresent( target -> party.getPartyMembers()
                .stream()
                .filter( m -> m.getUuid().equals( target.getUuid() ) )
                .findFirst()
                .ifPresentOrElse( ( member ) ->
                {
                    BuX.getInstance().getPartyManager().setPartyOwner( party, party.getOwner(), false );
                    BuX.getInstance().getPartyManager().setPartyOwner( party, member, true );


                    user.sendLangMessage(
                            "party.setowner.changed",
                            "{new-owner}", target.getUserName()
                    );
                    BuX.getInstance().getPartyManager().languageBroadcastToParty(
                            party,
                            "party.setowner.broadcast",
                            "{old-owner}", user.getName(),
                            "{new-owner}", target.getUserName()
                    );
                }, () ->
                {
                    user.sendLangMessage(
                            "party.setowner.not-in-party",
                            "{user}", target.getUserName()
                    );
                } ) );
    }

    @Override
    public String getDescription()
    {
        return "Changes the owner of the party.";
    }

    @Override
    public String getUsage()
    {
        return "/party setowner (user)";
    }
}
