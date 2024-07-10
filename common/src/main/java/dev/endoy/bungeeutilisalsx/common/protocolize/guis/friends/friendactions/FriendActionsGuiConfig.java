package dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendactions;

import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friend.FriendGuiConfigItem;

public class FriendActionsGuiConfig extends GuiConfig
{
    public FriendActionsGuiConfig()
    {
        super( "/configurations/gui/friends/friendactions.yml", FriendGuiConfigItem.class );
    }
}
