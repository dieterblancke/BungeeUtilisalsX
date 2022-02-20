package be.dieterblancke.bungeeutilisalsx.common.commands.party.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

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

        if ( !optionalPartyMember.isPresent() )
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
                "{party-owner}", party.getOwner().getUserName()
        );
        BuX.getInstance().getPartyManager().languageBroadcastToParty(
                party,
                "party.leave.left-broadcast",
                "{user}", user.getName()
        );

        if ( partyMember.isPartyOwner() && !party.isOwner( user.getUuid() ) )
        {
            BuX.getInstance().getPartyManager().languageBroadcastToParty(
                    party,
                    "party.leave.owner-left-broadcast",
                    "{party-old-owner}", user.getName(),
                    "{party-owner}", party.getOwner().getUserName()
            );
        }
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final Optional<Party> optionalParty = BuX.getInstance().getPartyManager().getCurrentPartyFor( user.getName() );

        if ( !optionalParty.isPresent() )
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
