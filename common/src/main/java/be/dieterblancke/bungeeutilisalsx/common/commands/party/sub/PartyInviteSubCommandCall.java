package be.dieterblancke.bungeeutilisalsx.common.commands.party.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyInvite;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.UserUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig.PartyRolePermission;

import java.util.Date;
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
                    user.sendLangMessage( "party.invite.already-in-party", "{user}", targetUser );
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
                    "{user}", user.getName()
            ) );

            user.sendLangMessage(
                    "party.invite.invite-success",
                    "{user}", target.getUserName()
            );
            BuX.getInstance().getPartyManager().languageBroadcastToParty(
                    party,
                    "party.invite.invited-broadcast",
                    "{user}", target.getUserName()
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
