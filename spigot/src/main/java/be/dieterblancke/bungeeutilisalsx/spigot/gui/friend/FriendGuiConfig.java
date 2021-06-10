package be.dieterblancke.bungeeutilisalsx.spigot.gui.friend;

import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfig;

public class FriendGuiConfig extends GuiConfig
{
    public FriendGuiConfig()
    {
        super( "/gui/friends.yml", FriendGuiConfigItem.class );
    }
}
