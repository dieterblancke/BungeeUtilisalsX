package com.dbsoftwares.bungeeutilisalsx.spigot.gui.friend;

import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.config.GuiConfig;

public class FriendGuiConfig extends GuiConfig
{
    public FriendGuiConfig()
    {
        super( "/gui/friends.yml", FriendGuiConfigItem.class );
    }
}
