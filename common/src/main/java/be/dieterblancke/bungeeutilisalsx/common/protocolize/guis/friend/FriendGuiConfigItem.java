package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friend;

import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItemStack;
import com.dbsoftwares.configuration.api.ISection;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode( callSuper = true )
public class FriendGuiConfigItem extends GuiConfigItem
{

    private final boolean friendItem;
    private final GuiConfigItemStack offlineItem;
    private final GuiConfigItemStack onlineItem;

    public FriendGuiConfigItem( final GuiConfig guiConfig, final ISection section )
    {
        super( guiConfig, section );

        this.friendItem = section.exists( "friend-slots" ) && section.getBoolean( "friend-slots" );

        if ( this.friendItem )
        {
            this.offlineItem = section.exists( "offlineitem" ) ? new GuiConfigItemStack( section.getSection( "offlineitem" ) ) : null;
            this.onlineItem = section.exists( "onlineitem" ) ? new GuiConfigItemStack( section.getSection( "onlineitem" ) ) : null;
        }
        else
        {
            this.offlineItem = null;
            this.onlineItem = null;
        }
    }
}
