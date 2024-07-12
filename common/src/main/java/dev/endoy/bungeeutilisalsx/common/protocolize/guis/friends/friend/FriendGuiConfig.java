package dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friend;

import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;

public class FriendGuiConfig extends GuiConfig
{
    public FriendGuiConfig()
    {
        super( "/configurations/gui/friends/friends.yml", FriendGuiConfigItem.class );
    }
}
