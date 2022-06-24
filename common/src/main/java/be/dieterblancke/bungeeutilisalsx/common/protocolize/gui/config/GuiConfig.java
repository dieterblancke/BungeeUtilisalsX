package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import be.dieterblancke.configuration.api.ISection;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode( callSuper = true )
public class GuiConfig extends Config
{

    private final Class<? extends GuiConfigItem> configItemClass;
    private final String title;
    private final int rows;
    private final String permission;
    private final List<GuiConfigItem> items = new ArrayList<>();

    public GuiConfig( final String fileLocation, final Class<? extends GuiConfigItem> configItemClass )
    {
        super( fileLocation );
        super.load();

        this.configItemClass = configItemClass;
        this.title = super.getConfig().getString( "title" );
        this.rows = super.getConfig().getInteger( "rows" );
        this.permission = super.getConfig().exists( "permission" ) ? super.getConfig().getString( "permission" ) : null;

        this.loadItems();
    }

    private void loadItems()
    {
        final List<ISection> contents = super.getConfig().getSectionList( "contents" );

        for ( ISection content : contents )
        {
            try
            {
                this.items.add( configItemClass.getConstructor( GuiConfig.class, ISection.class ).newInstance( this, content ) );
            }
            catch ( InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e )
            {
                e.printStackTrace();
            }
        }
    }
}
