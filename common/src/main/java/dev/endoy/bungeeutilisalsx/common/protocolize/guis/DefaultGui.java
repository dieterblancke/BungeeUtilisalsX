package dev.endoy.bungeeutilisalsx.common.protocolize.guis;

import dev.endoy.bungeeutilisalsx.common.api.friends.FriendRequestType;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friend.FriendGuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendactions.FriendActionsGuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.MainFriendRequestsGuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.request.FriendRequestsGuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.opener.*;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.parties.PartyGuiConfig;

import java.util.function.Supplier;

public enum DefaultGui
{

    FRIEND(
            FriendGuiConfig::new,
            FriendGuiOpener::new
    ),
    FRIENDACTIONS(
            FriendActionsGuiConfig::new,
            FriendActionsGuiOpener::new
    ),
    FRIENDREQUESTS(
            MainFriendRequestsGuiConfig::new,
            MainFriendRequestsGuiOpener::new
    ),
    INCOMINGFRIENDREQUESTS(
            () -> new FriendRequestsGuiConfig( FriendRequestType.INCOMING ),
            IncomingFriendRequestsGuiOpener::new
    ),
    OUTGOINGFRIENDREQUESTS(
            () -> new FriendRequestsGuiConfig( FriendRequestType.OUTGOING ),
            OutgoingFriendRequestsGuiOpener::new
    ),
    PARTY(
            PartyGuiConfig::new,
            PartyGuiOpener::new
    ),
    CUSTOM(
            () ->
            {
                new GuiConfig( "/configurations/gui/custom/test.yml", GuiConfigItem.class );
                return null;
            },
            CustomGuiOpener::new
    );

    private final Supplier<GuiConfig> configSupplier;
    private final Supplier<GuiOpener> guiOpenerSupplier;
    private GuiConfig config;

    DefaultGui( final Supplier<GuiConfig> configSupplier, final Supplier<GuiOpener> guiOpenerSupplier )
    {
        this.configSupplier = configSupplier;
        this.guiOpenerSupplier = guiOpenerSupplier;
    }

    public void loadConfig()
    {
        this.config = configSupplier.get();
    }

    public <T extends GuiConfig> T getConfig()
    {
        return (T) config;
    }

    public Supplier<GuiOpener> getGuiOpenerSupplier()
    {
        return guiOpenerSupplier;
    }
}
