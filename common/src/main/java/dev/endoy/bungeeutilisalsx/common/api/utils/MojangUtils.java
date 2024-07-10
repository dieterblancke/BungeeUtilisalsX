package dev.endoy.bungeeutilisalsx.common.api.utils;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

public class MojangUtils
{

    private static final Gson gson = new Gson();

    public static String getUuid( final String name )
    {
        try
        {
            final URL url = new URL( getNameToUuidUrl() + name );
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty( "User-Agent", "BungeeUtilisals/" + BuX.getInstance().getVersion() );

            try ( final InputStream input = conn.getInputStream();
                  final InputStreamReader isr = new InputStreamReader( input ) )
            {
                final MojangProfile profile = gson.fromJson( isr, MojangProfile.class );

                if ( profile != null )
                {
                    return profile.getUuid();
                }
            }
        }
        catch ( final IOException e )
        {
            BuX.getLogger().log( Level.SEVERE, "Could not retrieve uuid of " + name + ": ", e );
        }
        return null;
    }

    public static String getName( final UUID uuid )
    {
        try
        {
            final URL url = new URL( getUuidToNameUrl() + uuid.toString().replace( "-", "" ) );
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty( "User-Agent", "BungeeUtilisals/" + BuX.getInstance().getVersion() );

            try ( final InputStream input = conn.getInputStream();
                  final InputStreamReader isr = new InputStreamReader( input ) )
            {
                final MojangProfile profile = gson.fromJson( isr, MojangProfile.class );

                if ( profile != null )
                {
                    return profile.getName();
                }
            }
        }
        catch ( final IOException e )
        {
            BuX.getLogger().log( Level.SEVERE, "Could not retrieve name of " + uuid + ": ", e );
        }
        return null;
    }

    private static String getNameToUuidUrl()
    {
        return ConfigFiles.CONFIG.getConfig().get( "urls.name-to-uuid", "https://api.minetools.eu/uuid/" );
    }

    private static String getUuidToNameUrl()
    {
        return ConfigFiles.CONFIG.getConfig().get( "urls.uuid-to-name", "https://api.minetools.eu/uuid/" );
    }

    public static class MojangProfile
    {

        private String id;
        private String name;
        private String uuid;
        private String uuid_formatted;

        public String getName()
        {
            return name;
        }

        public String getUuid()
        {
            if ( id != null )
            {
                return id;
            }
            if ( uuid != null )
            {
                return uuid;
            }
            if ( uuid_formatted != null )
            {
                return uuid_formatted;
            }
            return null;
        }
    }
}