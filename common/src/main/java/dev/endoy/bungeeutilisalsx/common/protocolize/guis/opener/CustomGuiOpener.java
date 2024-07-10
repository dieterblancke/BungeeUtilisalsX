package dev.endoy.bungeeutilisalsx.common.protocolize.guis.opener;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.Gui;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.custom.CustomGuiItemProvider;
import com.google.common.base.Strings;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CustomGuiOpener extends GuiOpener
{

    private final Map<String, GuiConfig> configCache = new HashMap<>();

    public CustomGuiOpener()
    {
        super( "custom" );
    }

    @Override
    public void openGui( final User user, final String[] args )
    {
        if ( args.length == 0 )
        {
            return;
        }
        final String guiName = args[0];
        final String fileLocation = "/gui/custom/" + guiName + ".yml";

        if ( !new File( BuX.getInstance().getDataFolder(), fileLocation ).exists() )
        {
            user.sendLangMessage( "general-commands.opengui.gui-not-found", MessagePlaceholders.create().append( "gui", guiName ) );
            return;
        }

        final GuiConfig config = configCache.computeIfAbsent(
                guiName.toLowerCase(),
                name -> new GuiConfig( fileLocation, GuiConfigItem.class )
        );

        if ( !Strings.isNullOrEmpty( config.getPermission() ) && !user.hasPermission( config.getPermission() ) )
        {
            user.sendLangMessage( "no-permission" );
            return;
        }

        final Gui gui = Gui.builder()
                .itemProvider( new CustomGuiItemProvider( user, config ) )
                .rows( config.getRows() )
                .title( config.getTitle() )
                .user( user )
                .build();

        gui.open();
    }

    @Override
    public void reload()
    {
        this.configCache.clear();
    }
}
