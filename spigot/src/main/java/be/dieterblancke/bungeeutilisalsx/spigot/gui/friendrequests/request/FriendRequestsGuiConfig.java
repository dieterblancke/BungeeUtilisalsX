package be.dieterblancke.bungeeutilisalsx.spigot.gui.friendrequests.request;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequestType;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfig;

public class FriendRequestsGuiConfig extends GuiConfig
{
    public FriendRequestsGuiConfig( final FriendRequestType type )
    {
        super( "/gui/" + type.toString().toLowerCase() + "friendrequests.yml", FriendRequestGuiConfigItem.class );
    }
}
