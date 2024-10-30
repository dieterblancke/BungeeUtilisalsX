package dev.endoy.bungeeutilisalsx.common.placeholders;

import dev.endoy.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.PlaceHolderPack;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.event.InputPlaceHolderEvent;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.event.handler.InputPlaceHolderEventHandler;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import dev.endoy.configuration.api.IConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

public class InputPlaceHolders implements PlaceHolderPack
{

    @Override
    public void loadPack()
    {
        PlaceHolderAPI.addPlaceHolder( false, "timeleft", new InputPlaceHolderEventHandler()
        {
            @Override
            public String getReplacement( InputPlaceHolderEvent event )
            {
                final IConfiguration configuration = Utils.getLanguageConfiguration( event.getUser() ).getConfig();
                final SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy kk:mm:ss" );

                try
                {
                    final Date parsedDate = dateFormat.parse( event.getArgument() );
                    final Date date = ConfigFiles.CONFIG.isEnabled( "timezone", false )
                        ? Date.from( parsedDate.toInstant().atZone( ZoneId.of( ConfigFiles.CONFIG.getConfig().getString( "timezone.zone" ) ) ).toInstant() )
                        : parsedDate;

                    return Utils.getTimeLeft(
                        configuration.getString( "placeholders.timeleft" ),
                        date
                    );
                }
                catch ( ParseException e )
                {
                    return "";
                }
            }
        } );
        PlaceHolderAPI.addPlaceHolder( false, "getcount", new InputPlaceHolderEventHandler()
        {
            @Override
            public String getReplacement( InputPlaceHolderEvent event )
            {
                final Optional<ServerGroup> serverGroup = ConfigFiles.SERVERGROUPS.getServer( event.getArgument() );

                if ( serverGroup.isEmpty() )
                {
                    return "0";
                }

                return String.valueOf( serverGroup.get().getPlayers() );
            }
        } );
    }
}