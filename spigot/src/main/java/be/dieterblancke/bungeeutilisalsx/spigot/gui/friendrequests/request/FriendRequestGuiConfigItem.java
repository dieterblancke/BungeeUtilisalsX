package be.dieterblancke.bungeeutilisalsx.spigot.gui.friendrequests.request;

import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfigItem;
import com.dbsoftwares.configuration.api.ISection;
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
