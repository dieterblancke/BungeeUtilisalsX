package dev.endoy.bungeeutilisalsx.common.commands.party.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;
import java.util.Optional;

public class PartyChatSubCommandCall implements CommandCall
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

        if ( !args.isEmpty() )
        {
            BuX.getInstance().getPartyManager().languageBroadcastToParty(
                party,
                "party.chat.format",
                MessagePlaceholders.create()
                    .append( "user", user.getName() )
                    .append( "message", String.join( " ", args ) )
            );
        }
        else
        {
            party.getPartyMembers()
                .stream()
                .filter( m -> m.getUuid().equals( user.getUuid() ) )
                .findFirst()
                .ifPresentOrElse( member ->
                {
                    final boolean chatMode = !member.isChat();
                    BuX.getInstance().getPartyManager().setChatMode( party, member, chatMode );

                    user.sendLangMessage( "party.chat." + ( chatMode ? "enabled" : "disabled" ) );
                }, () ->
                {
                    user.sendLangMessage( "party.not-in-party" );
                } );

        }
    }

    @Override
    public String getDescription()
    {
        return "Toggles party chat mode or sends a chat message to the party.";
    }

    @Override
    public String getUsage()
    {
        return "/party chat [message]";
    }
}
