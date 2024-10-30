package dev.endoy.bungeeutilisalsx.common.commands.party.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyInvite;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyUtils;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.UserUtils;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig.PartyRolePermission;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class PartyInviteSubCommandCall implements CommandCall
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

        if ( args.size() != 1 )
        {
            user.sendLangMessage( "party.invite.usage" );
            return;
        }
        if ( !PartyUtils.hasPermission( party, user, PartyRolePermission.INVITE ) )
        {
            user.sendLangMessage( "party.invite.not-allowed" );
            return;
        }
        if ( party.isFull() )
        {
            user.sendLangMessage( "party.your-party-full" );
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

            final Optional<Party> currentParty = BuX.getInstance().getPartyManager().getCurrentPartyFor( target.getUserName() );

            if ( currentParty.isPresent() )
            {
                if ( currentParty.get().getUuid().equals( party.getUuid() ) )
                {
                    user.sendLangMessage( "party.invite.already-in-party", MessagePlaceholders.create().append( "user", targetUser ) );
                    return;
                }
                else if ( !ConfigFiles.PARTY_CONFIG.getConfig().getBoolean( "allow-invites-to-members-already-in-party" ) )
                {
                    user.sendLangMessage( "party.invite.already-in-other-party" );
                    return;
                }
            }

            BuX.getInstance().getPartyManager().addInvitationToParty(
                party,
                new PartyInvite( new Date(), target.getUuid(), target.getUserName(), user.getUuid() )
            );

            BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                target.getUserName(),
                "party.invite.invited",
                MessagePlaceholders.create()
                    .append( "user", user.getName() )
            ) );

            user.sendLangMessage(
                "party.invite.invite-success",
                MessagePlaceholders.create()
                    .append( "user", target.getUserName() )
            );
            BuX.getInstance().getPartyManager().languageBroadcastToParty(
                party,
                "party.invite.invited-broadcast",
                MessagePlaceholders.create()
                    .append( "user", target.getUserName() )
            );
        } );
    }

    @Override
    public String getDescription()
    {
        return "Invites someone to your current party.";
    }

    @Override
    public String getUsage()
    {
        return "/party invite (user)";
    }
}
