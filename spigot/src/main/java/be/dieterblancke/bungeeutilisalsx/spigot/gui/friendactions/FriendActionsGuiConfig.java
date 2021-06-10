package be.dieterblancke.bungeeutilisalsx.spigot.gui.friendactions;

import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfigItem;

public class FriendActionsGuiConfig extends GuiConfig
{
    public FriendActionsGuiConfig()
    {
        super( "/gui/friendactions.yml", FriendGuiConfigItem.class );
    }
}
