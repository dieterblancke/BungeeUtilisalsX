package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests;

import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;

public class MainFriendRequestsGuiConfig extends GuiConfig
{
    public MainFriendRequestsGuiConfig()
    {
        super( "/configurations/gui/friends/friendrequests.yml", GuiConfigItem.class );
    }
}
