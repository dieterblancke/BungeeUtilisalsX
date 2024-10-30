package dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.request;

import dev.endoy.bungeeutilisalsx.common.api.friends.FriendRequestType;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;

public class FriendRequestsGuiConfig extends GuiConfig
{

    public FriendRequestsGuiConfig( final FriendRequestType type )
    {
        super( "/configurations/gui/friends/" + type.toString().toLowerCase() + "friendrequests.yml", FriendRequestGuiConfigItem.class );
    }

}
