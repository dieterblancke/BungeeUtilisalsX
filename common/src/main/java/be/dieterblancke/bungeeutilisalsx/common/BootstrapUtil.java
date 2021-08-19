package be.dieterblancke.bungeeutilisalsx.common;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.LibraryClassLoader;
import be.dieterblancke.bungeeutilisalsx.common.library.Library;
import be.dieterblancke.bungeeutilisalsx.common.library.StandardLibrary;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.util.logging.Logger;

public class BootstrapUtil
{

    @Getter
    private static File dataFolder;

    @SneakyThrows
    public static void loadLibraries( final File dataFolder, final LibraryClassLoader libraryClassLoader, final Logger logger )
    {
        BootstrapUtil.dataFolder = dataFolder;
        ensureRelocatedLibraries( dataFolder, logger );

        logger.info( "Loading libraries ..." );

        for ( StandardLibrary standardLibrary : StandardLibrary.values() )
        {
            final Library library = standardLibrary.getLibrary();

            if ( library.isToLoad() && !library.isPresent() )
            {
                library.load( dataFolder, libraryClassLoader, logger );
            }
        }
        logger.info( "Libraries have been loaded." );
    }

    private static void ensureRelocatedLibraries( final File dataFolder, final Logger logger )
    {
        final File folder = new File( dataFolder, "libraries" );

        if ( folder.exists() )
        {
            final File originalFolder = new File( folder, "original" );
            final File relocationFolder = new File( folder, "relocated" );
            final File[] files = folder.listFiles();

            if ( !originalFolder.exists() && !relocationFolder.exists() )
            {
                logger.info( "Detected outdated library structure. Removing all libraries ..." );
                originalFolder.mkdir();
                relocationFolder.mkdir();

                for ( File file : files )
                {
                    file.delete();
                }
            }
        }
    }

}
