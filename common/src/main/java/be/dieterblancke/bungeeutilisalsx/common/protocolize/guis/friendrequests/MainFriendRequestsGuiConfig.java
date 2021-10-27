package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friendrequests;

import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;

public class MainFriendRequestsGuiConfig extends GuiConfig
{
    public MainFriendRequestsGuiConfig()
    {
        super( "/gui/friendrequests.yml", GuiConfigItem.class );
    }
}
