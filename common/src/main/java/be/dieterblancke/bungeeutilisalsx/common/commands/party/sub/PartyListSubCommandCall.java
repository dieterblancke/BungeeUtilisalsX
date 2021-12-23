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
import be.dieterblancke.bungeeutilisalsx.common.api.party.*;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.PageUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.PageUtils.PageMessageInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.PageUtils.PageResponseHandler;

import java.util.List;
import java.util.Optional;

public class PartyListSubCommandCall implements CommandCall
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

        if ( args.size() > 0 )
        {
            final String type = args.get( 0 );

            if ( type.equalsIgnoreCase( "requests" ) )
            {
                this.sendJoinRequestsList( user, party, args );
                return;
            }
            else if ( type.equalsIgnoreCase( "invites" ) )
            {
                this.sendInvitesList( user, party, args );
                return;
            }
        }
        this.sendMembersList( user, party, args );
    }

    @Override
    public String getDescription()
    {
        return "Shows a list of members, invites or requests of your current party.";
    }

    @Override
    public String getUsage()
    {
        return "/party list (members / invites / requests) [page]";
    }

    private void sendMembersList( final User user, final Party party, final List<String> args )
    {
        PageUtils.sendPagedList( user, party.getPartyMembers(), args.size() == 2 ? args.get( 1 ) : "1", 10, new PageResponseHandler<>()
        {

            @Override
            public PageMessageInfo getEmptyListMessage()
            {
                return new PageMessageInfo( "party.list.members.empty" );
            }

            @Override
            public PageMessageInfo getHeaderMessage()
            {
                return new PageMessageInfo( "party.list.members.header" );
            }

            @Override
            public PageMessageInfo getItemMessage( final PartyMember member )
            {
                return new PageMessageInfo(
                        "party.list.members.item",
                        "{user}", member.getUserName(),
                        "{role}", PartyUtils.getRoleName( party, member.getUuid(), user.getLanguageConfig() ),
                        "{joinedAt}", Utils.formatDate( member.getJoinedAt(), user.getLanguageConfig().getConfig() )
                );
            }

            @Override
            public PageMessageInfo getFooterMessage()
            {
                return new PageMessageInfo( "party.list.members.footer" );
            }

            @Override
            public PageMessageInfo getInvalidPageMessage()
            {
                return new PageMessageInfo( "party.list.members.wrong-page" );
            }
        } );
    }

    private void sendInvitesList( final User user, final Party party, final List<String> args )
    {
        PageUtils.sendPagedList( user, party.getSentInvites(), args.size() == 2 ? args.get( 1 ) : "1", 10, new PageResponseHandler<>()
        {

            @Override
            public PageMessageInfo getEmptyListMessage()
            {
                return new PageMessageInfo( "party.list.invites.empty" );
            }

            @Override
            public PageMessageInfo getHeaderMessage()
            {
                return new PageMessageInfo( "party.list.invites.header" );
            }

            @Override
            public PageMessageInfo getItemMessage( final PartyInvite invite )
            {
                return new PageMessageInfo(
                        "party.list.invites.item",
                        "{user}", invite.getInviteeName(),
                        "{invitedAt}", Utils.formatDate( invite.getInvitedAt(), user.getLanguageConfig().getConfig() )
                );
            }

            @Override
            public PageMessageInfo getFooterMessage()
            {
                return new PageMessageInfo( "party.list.invites.footer" );
            }

            @Override
            public PageMessageInfo getInvalidPageMessage()
            {
                return new PageMessageInfo( "party.list.invites.wrong-page" );
            }
        } );
    }

    private void sendJoinRequestsList( final User user, final Party party, final List<String> args )
    {
        PageUtils.sendPagedList( user, party.getJoinRequests(), args.size() == 2 ? args.get( 1 ) : "1", 10, new PageResponseHandler<>()
        {

            @Override
            public PageMessageInfo getEmptyListMessage()
            {
                return new PageMessageInfo( "party.list.requests.empty" );
            }

            @Override
            public PageMessageInfo getHeaderMessage()
            {
                return new PageMessageInfo( "party.list.requests.header" );
            }

            @Override
            public PageMessageInfo getItemMessage( final PartyJoinRequest request )
            {
                return new PageMessageInfo(
                        "party.list.requests.item",
                        "{user}", request.getRequesterName(),
                        "{requestedAt}", Utils.formatDate( request.getRequestedAt(), user.getLanguageConfig().getConfig() )
                );
            }

            @Override
            public PageMessageInfo getFooterMessage()
            {
                return new PageMessageInfo( "party.list.requests.footer" );
            }

            @Override
            public PageMessageInfo getInvalidPageMessage()
            {
                return new PageMessageInfo( "party.list.requests.wrong-page" );
            }
        } );
    }
}
