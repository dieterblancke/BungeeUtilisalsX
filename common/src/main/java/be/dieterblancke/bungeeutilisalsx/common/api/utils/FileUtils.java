package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Optional;

public class FileUtils
{

    private FileUtils()
    {
    }

    @SneakyThrows
    public static InputStream getResourceAsStream( final String path )
    {
        return Optional.ofNullable( BuX.class.getClassLoader().getResourceAsStream( path ) )
                .orElse( path.startsWith( "/" )
                        ? BuX.class.getClassLoader().getResourceAsStream( path.replaceFirst( "/", "" ) )
                        : BuX.class.getClassLoader().getResourceAsStream( "/" + path ) );
    }
}
