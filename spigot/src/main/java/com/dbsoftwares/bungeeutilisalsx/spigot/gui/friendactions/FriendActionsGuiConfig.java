package com.dbsoftwares.bungeeutilisalsx.spigot.gui.friendactions;

import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.config.GuiConfig;
import com.dbsoftwares.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfigItem;

public class FriendActionsGuiConfig extends GuiConfig
{
    public FriendActionsGuiConfig()
    {
        super( "/gui/friendactions.yml", FriendGuiConfigItem.class );
    }
}
