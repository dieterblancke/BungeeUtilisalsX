package be.dieterblancke.bungeeutilisalsx.common.commands.party.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PartyInfoSubCommandCall implements CommandCall
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

        user.sendLangMessage(
                "party.info",
                MessagePlaceholders.create()
                        .append( "party-owner", party.getOwner().getNickName() )
                        .append( "party-created-at", Utils.formatDate( party.getCreatedAt(), user.getLanguageConfig().getConfig() ) )
                        .append( "party-member-count", party.getPartyMembers().size() )
                        .append( "party-member-list", party.getPartyMembers().stream().map( PartyMember::getNickName ).collect( Collectors.joining( ", " ) ) )
                        .append( "party-invitation-count", party.getSentInvites().size() )
                        .append( "party-joinrequest-count", party.getJoinRequests().size() )
        );
    }

    @Override
    public String getDescription()
    {
        return "Shows basic information about your current party.";
    }

    @Override
    public String getUsage()
    {
        return "/party info";
    }
}
