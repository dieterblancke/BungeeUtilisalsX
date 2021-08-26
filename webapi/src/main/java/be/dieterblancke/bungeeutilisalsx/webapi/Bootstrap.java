package be.dieterblancke.bungeeutilisalsx.webapi;

import be.dieterblancke.bungeeutilisalsx.common.BootstrapUtil;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.UrlLibraryClassLoader;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import java.io.File;

@Log
@SpringBootApplication( exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
} )
public class Bootstrap
{

    @SneakyThrows
    public static void main( final String[] args )
    {
        BootstrapUtil.loadLibraries(
                new File( new File( Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI() ), "BungeeUtilisalsX" ),
                new UrlLibraryClassLoader(),
                log
        );

        SpringApplication.run( Bootstrap.class, args );
    }
}