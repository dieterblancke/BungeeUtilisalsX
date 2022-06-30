package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friendactions;

import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friend.FriendGuiConfigItem;

public class FriendActionsGuiConfig extends GuiConfig
{
    public FriendActionsGuiConfig()
    {
        super( "/configurations/gui/friends/friendactions.yml", FriendGuiConfigItem.class );
    }
}
