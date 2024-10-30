package dev.endoy.bungeeutilisalsx.common.protocolize.guis.parties;

import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyMember;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.PageableItemProvider;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;

import java.util.List;
import java.util.Optional;

public class PartyGuiItemProvider implements PageableItemProvider
{

    private final ItemPage[] pages;

    public PartyGuiItemProvider( final User user,
                                 final PartyGuiConfig config,
                                 final Party party,
                                 final List<PartyMember> members )
    {
        final int itemsPerPage = config.getItems().stream()
            .filter( item -> ( (PartyGuiConfigItem) item ).isMemberItem() )
            .mapToInt( item -> item.getSlots().size() )
            .sum();
        int pages = (int) Math.ceil( (double) members.size() / (double) itemsPerPage );
        if ( pages == 0 )
        {
            pages = 1;
        }
        this.pages = new ItemPage[pages];

        for ( int i = 0; i < pages; i++ )
        {
            final int max = ( i + 1 ) * itemsPerPage;

            this.pages[i] = new PartyItemPage(
                user,
                i,
                pages,
                config,
                party,
                members.isEmpty() ? members : members.size() <= max ? members : members.subList( i * itemsPerPage, max )
            );
        }
    }

    @Override
    public Optional<GuiItem> getItemAtSlot( final int page, final int rawSlot )
    {
        return this.getItemContents( page ).getItem( rawSlot );
    }

    @Override
    public ItemPage getItemContents( int page )
    {
        if ( page == 0 )
        {
            page = 1;
        }
        if ( page > pages.length )
        {
            page = pages.length;
        }
        return pages[page - 1];
    }

    @Override
    public int getPageAmount()
    {
        return pages.length;
    }
}
