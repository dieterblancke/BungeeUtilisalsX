package be.dieterblancke.bungeeutilisalsx.common.commands.party.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;
import java.util.Optional;

public class PartyChatSubCommandCall implements CommandCall
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

        if ( args.size() > 0 )
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
