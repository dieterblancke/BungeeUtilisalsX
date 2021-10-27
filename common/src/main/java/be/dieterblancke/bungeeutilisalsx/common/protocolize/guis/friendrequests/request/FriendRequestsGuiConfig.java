package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friendrequests.request;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequestType;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;

public class FriendRequestsGuiConfig extends GuiConfig
{

    public FriendRequestsGuiConfig( final FriendRequestType type )
    {
        super( "/gui/" + type.toString().toLowerCase() + "friendrequests.yml", FriendRequestGuiConfigItem.class );
    }

}
