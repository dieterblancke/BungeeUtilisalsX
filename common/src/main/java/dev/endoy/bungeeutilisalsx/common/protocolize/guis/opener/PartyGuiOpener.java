package dev.endoy.bungeeutilisalsx.common.protocolize.guis.opener;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyMember;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.Gui;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.DefaultGui;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.parties.PartyGuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.parties.PartyGuiItemProvider;

import java.util.List;
import java.util.Optional;

public class PartyGuiOpener extends GuiOpener
{

    public PartyGuiOpener()
    {
        super( "party" );
    }

    @Override
    public void openGui( final User user, final String[] args )
    {
        if ( !BuX.getInstance().isPartyManagerEnabled() )
        {
            user.sendLangMessage( "party.not-in-party" );
            return;
        }
        final Optional<Party> optionalParty = BuX.getInstance().getPartyManager().getCurrentPartyFor( user.getName() );

        if ( optionalParty.isEmpty() )
        {
            user.sendLangMessage( "party.not-in-party" );
            return;
        }
        final Party party = optionalParty.get();
        final List<PartyMember> partyMembers = party.getPartyMembers();
        final PartyGuiConfig config = DefaultGui.PARTY.getConfig();
        final Gui gui = Gui.builder()
            .itemProvider( new PartyGuiItemProvider( user, config, party, partyMembers ) )
            .rows( config.getRows() )
            .title( config.getTitle() )
            .user( user )
            .build();

        gui.open();
    }
}
