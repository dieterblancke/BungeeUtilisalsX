package be.dieterblancke.bungeeutilisalsx.common.bridge.redis;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.redis.RedisManager;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;

public final class RedisManagerFactory
{

    private RedisManagerFactory()
    {
    }

    public static RedisManager create()
    {
        final IConfiguration config = ConfigFiles.CONFIG.getConfig();
        final ISection bridging = config.getSection( "bridging" );

        if ( bridging.isList( "redis" ) )
        {
            BuX.getLogger().info( "Detected " + bridging.getList( "redis" ).size() + " configured connections, using ClusteredRedisManager." );
            return new ClusteredRedisManager( bridging );
        }

        BuX.getLogger().info( "Detected 1 configured connection, using StandaloneRedisManager." );
        return new StandardRedisManager( bridging );
    }
}
