package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.parties;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig.PartyRole;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig.PartyRolePermission;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PartyItemPage extends ItemPage
{

    public PartyItemPage( final User user,
                          final int page,
                          final int max,
                          final PartyGuiConfig guiConfig,
                          final List<PartyMember> partyMembers )
    {
        super( guiConfig.getRows() * 9 );

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            if ( ( (PartyGuiConfigItem) item ).isMemberItem() )
            {
                continue;
            }
            if ( !this.shouldShow( user, page, max, item ) )
            {
                continue;
            }
            for ( int slot : item.getSlots() )
            {
                super.setItem( slot, this.getGuiItem( user, item ) );
            }
        }

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            if ( !( (PartyGuiConfigItem) item ).isMemberItem() )
            {
                continue;
            }

            final Iterator<PartyMember> partyMemberIterator = partyMembers.iterator();
            for ( int slot : item.getSlots() )
            {
                if ( !partyMemberIterator.hasNext() )
                {
                    break;
                }
                final PartyMember member = partyMemberIterator.next();
                final String currentServer = Optional.ofNullable( BuX.getApi().getPlayerUtils().findPlayer( member.getUserName() ) )
                        .map( IProxyServer::getName ).orElse( null );

                super.setItem( slot, this.getPartyMemberGuiItem(
                        user,
                        (PartyGuiConfigItem) item,
                        member,
                        currentServer,
                        "{member}", member.getUserName(),
                        "{server}", currentServer == null ? "Unknown" : currentServer,
                        "{role}", Optional.ofNullable( member.getPartyRole() )
                                .map( PartyRole::getName )
                                .orElse( user.getLanguageConfig().getConfig().getString( "party.list.members.no-role" ) )
                ) );
            }
        }
    }

    @Override
    protected boolean shouldShow( final User user, final int page, final int max, final GuiConfigItem item )
    {
        if ( item.getShowIf().startsWith( "has-party-permission:" ) )
        {
            final PartyRolePermission partyRolePermission = Utils.valueOfOr(
                    PartyRolePermission.class,
                    item.getShowIf().replaceFirst( "has-party-permission:", "" ),
                    null
            );
            final Party party = BuX.getInstance().getPartyManager().getCurrentPartyFor( user.getName() ).orElse( null );

            if ( partyRolePermission != null && party != null )
            {
                return PartyUtils.hasPermission( party, user, partyRolePermission );
            }
        }
        else if ( item.getShowIf().equalsIgnoreCase( "is-party-owner" ) )
        {
            final Party party = BuX.getInstance().getPartyManager().getCurrentPartyFor( user.getName() ).orElse( null );

            return party != null && party.getOwner().getUuid().equals( user.getUuid() );
        }

        return super.shouldShow( user, page, max, item );
    }

    private GuiItem getPartyMemberGuiItem( final User user,
                                           final PartyGuiConfigItem item,
                                           final PartyMember member,
                                           final String currentServer,
                                           final Object... placeholders )
    {
        final boolean online = currentServer != null;
        final ItemStack itemStack = online
                ? item.getOnlineItem().buildItem( user, placeholders )
                : item.getOfflineItem().buildItem( user, placeholders );

        if ( itemStack.itemType() == ItemType.PLAYER_HEAD )
        {
            itemStack.nbtData().putString( "SkullOwner", member.getUserName() );
        }

        return this.getGuiItem( item.getAction(), item.getRightAction(), itemStack, placeholders );
    }
}
