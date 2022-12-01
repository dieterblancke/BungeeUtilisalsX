package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.opener;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.DefaultGui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.parties.PartyGuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.parties.PartyGuiItemProvider;

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
