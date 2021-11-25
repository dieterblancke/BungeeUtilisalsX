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
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyInvite;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyManager;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyAcceptSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final PartyManager partyManager = BuX.getInstance().getPartyManager();

        if ( args.size() != 1 )
        {
            user.sendLangMessage( "party.accept.usage" );
            return;
        }

        final Optional<Party> optionalParty = partyManager.getCurrentPartyFor( user.getName() );

        // Already in party checks
        if ( optionalParty.isPresent() )
        {
            if ( ConfigFiles.PARTY_CONFIG.getConfig().getBoolean( "allow-invites-to-members-already-in-party" ) )
            {
                PartyLeaveSubCommandCall.leaveParty( optionalParty.get(), user );
            }
            else
            {
                user.sendLangMessage( "party.accept.already-in-party" );
            }
            return;
        }

        // Check invite
        final Optional<Party> optionalInviterParty = partyManager.getCurrentPartyFor( args.get( 0 ) );
        if ( optionalInviterParty.isEmpty() )
        {
            user.sendLangMessage( "party.accept.no-party", "{user}", args.get( 0 ) );
            return;
        }

        final Party inviterParty = optionalInviterParty.get();
        final Optional<PartyInvite> optionalInvite = inviterParty.getSentInvites()
                .stream()
                .filter( invite -> invite.getInvitee().equals( user.getUuid() ) )
                .findAny();

        if ( optionalInvite.isEmpty() )
        {
            user.sendLangMessage( "party.accept.not-invited-to-party", "{user}", args.get( 0 ) );
            return;
        }

        // Add to party and remove invite
        final PartyInvite invite = optionalInvite.get();
        partyManager.removeInvitationFromParty( inviterParty, invite );

        final PartyMember partyMember = new PartyMember(
                user.getUuid(),
                user.getName(),
                new Date(),
                user.getName(),
                false,
                false,
                false
        );

        partyManager.addMemberToParty( inviterParty, partyMember );
        user.sendLangMessage(
                "party.accept.accepted",
                "{user}", args.get( 0 )
        );
        partyManager.languageBroadcastToParty(
                inviterParty,
                "party.accept.joined-party",
                "{user}", user.getName()
        );
    }

    @Override
    public String getDescription()
    {
        return "Accepts the party invite from a certain user.";
    }

    @Override
    public String getUsage()
    {
        return "/party accept (user)";
    }
}
