package be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class PunishmentsConfig extends Config
{

    public PunishmentsConfig( final String location )
    {
        super( location );
    }

    @Override
    public void load()
    {
        final File file = new File( BuX.getInstance().getDataFolder(), "punishments.yml" );
        final File folder = new File( BuX.getInstance().getDataFolder(), "punishments" );

        if ( file.exists() && !folder.exists() )
        {
            try
            {
                folder.mkdirs();
                Files.move( file, new File( folder, "punishments.yml" ) );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        super.load();
    }
}
