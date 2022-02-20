package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.request;

import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;
import be.dieterblancke.configuration.api.ISection;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode( callSuper = true )
public class FriendRequestGuiConfigItem extends GuiConfigItem
{

    private final boolean requestItem;

    public FriendRequestGuiConfigItem( final GuiConfig guiConfig, final ISection section )
    {
        super( guiConfig, section );

        this.requestItem = section.exists( "request-slots" ) && section.getBoolean( "request-slots" );
    }
}
