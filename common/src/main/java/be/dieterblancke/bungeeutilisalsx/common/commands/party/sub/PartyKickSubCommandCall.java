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
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;
import java.util.Optional;

public class PartyKickSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() != 1 )
        {
            user.sendLangMessage( "party.kick.usage" );
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
            user.sendLangMessage( "party.kick.not-allowed" );
            return;
        }

        final String targetName = args.get( 0 );

        party.getPartyMembers()
                .stream()
                .filter( m -> m.getUserName().equalsIgnoreCase( targetName ) || m.getNickName().equalsIgnoreCase( targetName ) )
                .findFirst()
                .ifPresentOrElse( member ->
                {
                    BuX.getInstance().getPartyManager().removeMemberFromParty( party, member );

                    user.sendLangMessage(
                            "party.kick.kick",
                            "{kickedUser}", member.getUserName()
                    );

                    BuX.getInstance().getPartyManager().languageBroadcastToParty(
                            party,
                            "party.kick.kicked-broadcast",
                            "{kickedUser}", member.getUserName(),
                            "{user}", user.getName()
                    );

                    BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                            member.getUuid(),
                            "party.kick.kicked",
                            "{user}", user.getName()
                    ) );
                }, () -> user.sendLangMessage( "party.kick.not-in-party" ) );
    }

    @Override
    public String getDescription()
    {
        return "Kicks a member from the party.";
    }

    @Override
    public String getUsage()
    {
        return "/party kick (user)";
    }
}
