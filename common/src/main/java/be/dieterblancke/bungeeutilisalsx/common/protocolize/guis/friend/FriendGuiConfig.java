package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friend;

import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;

public class FriendGuiConfig extends GuiConfig
{
    public FriendGuiConfig()
    {
        super( "/gui/friends.yml", FriendGuiConfigItem.class );
    }
}
