package be.dieterblancke.bungeeutilisalsx.common.placeholders;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderPack;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.configuration.api.IConfiguration;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DefaultPlaceHolders implements PlaceHolderPack
{

    @Override
    public void loadPack()
    {
        // Proxy PlaceHolders
        PlaceHolderAPI.addPlaceHolder( "{proxy_online}", false,
                event -> String.valueOf( BuX.getApi().getPlayerUtils().getTotalCount() ) );
        PlaceHolderAPI.addPlaceHolder( "{proxy_max}", false,
                event -> String.valueOf( BuX.getInstance().proxyOperations().getMaxPlayers() ) );

        PlaceHolderAPI.addPlaceHolder( "{date}", false, this::getCurrentDate );
        PlaceHolderAPI.addPlaceHolder( "{time}", false, this::getCurrentTime );
        PlaceHolderAPI.addPlaceHolder( "{datetime}", false, this::getCurrentDateTime );
    }

    private String getCurrentDate( final PlaceHolderEvent event )
    {
        return this.getCurrentTime( event.getUser(), "date" );
    }

    private String getCurrentTime( final PlaceHolderEvent event )
    {
        return this.getCurrentTime( event.getUser(), "time" );
    }

    private String getCurrentDateTime( final PlaceHolderEvent event )
    {
        return this.getCurrentTime( event.getUser(), "datetime" );
    }

    private String getCurrentTime( final User user, final String type )
    {
        final IConfiguration configuration = Utils.getLanguageConfiguration( user ).getConfig();

        if ( configuration == null )
        {
            return "";
        }
        final String format = configuration.getString( "placeholders.format." + type );
        if ( format == null )
        {
            return "";
        }
        final ZonedDateTime localDateTime = ConfigFiles.CONFIG.isEnabled( "timezone", false )
                ? ZonedDateTime.now( ZoneId.of( ConfigFiles.CONFIG.getConfig().getString( "timezone.zone" ) ) )
                : ZonedDateTime.now();

        return localDateTime.format( DateTimeFormatter.ofPattern( format ) );
    }
}