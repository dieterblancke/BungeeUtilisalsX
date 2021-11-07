package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friendactions;

import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friend.FriendGuiConfigItem;

public class FriendActionsGuiConfig extends GuiConfig
{
    public FriendActionsGuiConfig()
    {
        super( "/gui/friendactions.yml", FriendGuiConfigItem.class );
    }
}
