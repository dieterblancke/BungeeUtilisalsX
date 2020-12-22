package be.dieterblancke.bungeeutilisalsx.spigot.gui;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequestType;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friendactions.FriendActionsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friendrequests.MainFriendRequestsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friendrequests.request.FriendRequestsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.opener.*;

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
