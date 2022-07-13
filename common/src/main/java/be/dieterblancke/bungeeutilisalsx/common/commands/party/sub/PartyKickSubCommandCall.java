package be.dieterblancke.bungeeutilisalsx.common.commands.party.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig.PartyRolePermission;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

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

        if ( optionalParty.isEmpty() )
        {
            user.sendLangMessage( "party.not-in-party" );
            return;
        }
        final Party party = optionalParty.get();

        if ( !PartyUtils.hasPermission( party, user, PartyRolePermission.INVITE ) )
        {
            user.sendLangMessage( "party.kick.not-allowed" );
            return;
        }
        final PartyMember currentMember = party.getMemberByUuid( user.getUuid() ).orElse( null );
        final String targetName = args.get( 0 );

        party.getPartyMembers()
                .stream()
                .filter( m -> m.getUserName().equalsIgnoreCase( targetName ) || m.getNickName().equalsIgnoreCase( targetName ) )
                .findFirst()
                .ifPresentOrElse( member ->
                {
                    if ( !party.isOwner( currentMember.getUuid() )
                            && ( party.isOwner( member.getUuid() ) || currentMember.getPartyRolePriority() <= member.getPartyRolePriority() ) )
                    {
                        user.sendLangMessage(
                                "party.kick.cannot-kick",
                                MessagePlaceholders.create()
                                        .append( "user", member.getUserName() )
                        );
                        return;
                    }

                    BuX.getInstance().getPartyManager().removeMemberFromParty( party, member );

                    user.sendLangMessage(
                            "party.kick.kick",
                            MessagePlaceholders.create()
                                    .append( "kickedUser", member.getUserName() )
                    );

                    BuX.getInstance().getPartyManager().languageBroadcastToParty(
                            party,
                            "party.kick.kicked-broadcast",
                            MessagePlaceholders.create()
                                    .append( "kickedUser", member.getUserName() )
                                    .append( "user", user.getName() )
                    );

                    BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                            member.getUuid(),
                            "party.kick.kicked",
                            MessagePlaceholders.create()
                                    .append( "user", user.getName() )
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
