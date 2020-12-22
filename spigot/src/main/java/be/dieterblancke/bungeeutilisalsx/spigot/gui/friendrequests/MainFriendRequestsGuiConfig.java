package be.dieterblancke.bungeeutilisalsx.spigot.gui.friendrequests;

import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfigItem;

public class MainFriendRequestsGuiConfig extends GuiConfig
{
    public MainFriendRequestsGuiConfig()
    {
        super( "/gui/friendrequests.yml", GuiConfigItem.class );
    }
}
