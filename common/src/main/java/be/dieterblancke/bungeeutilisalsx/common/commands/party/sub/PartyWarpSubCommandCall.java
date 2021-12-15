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
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig.PartyRolePermission;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;

import java.util.List;
import java.util.Optional;

public class PartyWarpSubCommandCall implements CommandCall
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

        if ( !PartyUtils.hasPermission( party, user, PartyRolePermission.WARP ) )
        {
            user.sendLangMessage( "party.warp.not-allowed" );
            return;
        }

        user.getCurrentServer().ifPresentOrElse( currentServer ->
        {
            final List<PartyMember> partyMembersToWarp;
            if ( args.size() == 1 )
            {
                final String targetName = args.get( 0 );

                partyMembersToWarp = party.getPartyMembers()
                        .stream()
                        .filter( m -> m.getUserName().equalsIgnoreCase( targetName ) || m.getNickName().equalsIgnoreCase( targetName ) )
                        .filter( m ->
                        {
                            final String currentMemberServer = Optional.ofNullable(
                                    BuX.getApi().getPlayerUtils().findPlayer( m.getUserName() )
                            ).map( IProxyServer::getName ).orElse( "" );

                            return !currentServer.getName().equals( currentMemberServer )
                                    && ConfigFiles.PARTY_CONFIG.canWarpFrom( currentMemberServer );
                        } )
                        .limit( 1 )
                        .toList();
            }
            else
            {
                partyMembersToWarp = party.getPartyMembers()
                        .stream()
                        .filter( m ->
                        {
                            final String currentMemberServer = Optional.ofNullable(
                                    BuX.getApi().getPlayerUtils().findPlayer( m.getUserName() )
                            ).map( IProxyServer::getName ).orElse( "" );

                            return !currentServer.getName().equals( currentMemberServer )
                                    && ConfigFiles.PARTY_CONFIG.canWarpFrom( currentMemberServer );
                        } )
                        .toList();
            }

            if ( !partyMembersToWarp.isEmpty() )
            {
                // TODO: warp party members (job)
            }
            else
            {
                user.sendLangMessage( "party.warp.nobody-to-warp" );
            }
        }, () -> user.sendLangMessage( "party.warp.failed" ) );
    }

    @Override
    public String getDescription()
    {
        return "Warps all party members to your current server.";
    }

    @Override
    public String getUsage()
    {
        return "/party warp [user]";
    }
}
