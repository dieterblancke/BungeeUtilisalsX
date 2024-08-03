package dev.endoy.bungeeutilisalsx.common.commands.party.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.PartyWarpMembersJob;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyMember;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyUtils;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig.PartyRolePermission;

import java.util.List;
import java.util.Optional;

public class PartyWarpSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final Optional<Party> optionalParty = BuX.getInstance().getPartyManager().getCurrentPartyFor( user.getName() );

        if ( optionalParty.isEmpty() )
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
                BuX.getInstance().getJobManager().executeJob( new PartyWarpMembersJob(
                    party.getUuid(),
                    partyMembersToWarp
                        .stream()
                        .map( PartyMember::getUuid )
                        .toList(),
                    currentServer.getName()
                ) );

                user.sendLangMessage( "party.warp.warping" );
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
