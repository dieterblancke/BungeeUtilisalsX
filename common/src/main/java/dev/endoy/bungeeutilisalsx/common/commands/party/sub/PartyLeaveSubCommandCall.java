package dev.endoy.bungeeutilisalsx.common.commands.party.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyMember;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;
import java.util.Optional;

public class PartyLeaveSubCommandCall implements CommandCall
{

    public static void leaveParty( final Party party, final User user )
    {
        final Optional<PartyMember> optionalPartyMember = party.getPartyMembers()
                .stream()
                .filter( member -> member.getUuid().equals( user.getUuid() ) )
                .findFirst();

        if ( optionalPartyMember.isEmpty() )
        {
            return;
        }
        final PartyMember partyMember = optionalPartyMember.get();

        BuX.getInstance().getPartyManager().removeMemberFromParty(
                party,
                partyMember
        );

        user.sendLangMessage(
                "party.leave.left",
                MessagePlaceholders.create()
                        .append( "party-owner", party.getOwner().getUserName() )
        );
        BuX.getInstance().getPartyManager().languageBroadcastToParty(
                party,
                "party.leave.left-broadcast",
                MessagePlaceholders.create()
                        .append( "user", user.getName() )
        );

        if ( partyMember.isPartyOwner() && !party.isOwner( user.getUuid() ) )
        {
            BuX.getInstance().getPartyManager().languageBroadcastToParty(
                    party,
                    "party.leave.owner-left-broadcast",
                    MessagePlaceholders.create()
                            .append( "party-old-owner", user.getName() )
                            .append( "{party-owner}", party.getOwner().getUserName() )
            );
        }
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final Optional<Party> optionalParty = BuX.getInstance().getPartyManager().getCurrentPartyFor( user.getName() );

        if ( optionalParty.isEmpty() )
        {
            user.sendLangMessage( "party.not-in-party" );
            return;
        }

        leaveParty( optionalParty.get(), user );
    }

    @Override
    public String getDescription()
    {
        return "Leaves your current party.";
    }

    @Override
    public String getUsage()
    {
        return "/party leave";
    }
}
