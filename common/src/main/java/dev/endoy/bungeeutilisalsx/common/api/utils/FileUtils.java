package dev.endoy.bungeeutilisalsx.common.api.utils;

import dev.endoy.bungeeutilisalsx.common.BuX;
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
