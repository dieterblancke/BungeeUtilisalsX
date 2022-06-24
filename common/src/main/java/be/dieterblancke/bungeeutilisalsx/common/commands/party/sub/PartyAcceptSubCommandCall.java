package be.dieterblancke.bungeeutilisalsx.common.commands.party.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyInvite;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyManager;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
                return;
            }
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

        if ( inviterParty.isFull() )
        {
            user.sendLangMessage(
                    "party.other-party-full",
                    "{user}", inviterParty.getOwner().getNickName()
            );
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
                ConfigFiles.PARTY_CONFIG.getDefaultRole(),
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
