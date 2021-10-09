package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

public class FileUtils
{

    private FileUtils()
    {
    }

    public static InputStream getResourceAsStream( final String path )
    {
        return Optional.ofNullable( BuX.class.getResourceAsStream( path ) )
                .orElse( path.startsWith( File.separator )
                        ? BuX.class.getResourceAsStream( path.replaceFirst( File.separator, "" ) )
                        : BuX.class.getResourceAsStream( File.separator + path ) );
    }
}
