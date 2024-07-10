package dev.endoy.bungeeutilisalsx.common.commands.party.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;
import java.util.Optional;

public class PartyDisbandSubCommandCall implements CommandCall
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

        if ( !party.isOwner( user.getUuid() ) )
        {
            user.sendLangMessage( "party.disband.not-allowed" );
            return;
        }

        BuX.getInstance().getPartyManager().languageBroadcastToParty(
                party,
                "party.disband.broadcast",
                MessagePlaceholders.create()
                        .append( "user", user.getName() )
        );

        BuX.getInstance().getPartyManager().removeParty( party );
        user.sendLangMessage( "party.disband.disbanded" );
    }

    @Override
    public String getDescription()
    {
        return "Disbands the party.";
    }

    @Override
    public String getUsage()
    {
        return "/party disband";
    }
}
