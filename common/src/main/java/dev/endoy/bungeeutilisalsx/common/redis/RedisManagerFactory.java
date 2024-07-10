package dev.endoy.bungeeutilisalsx.common.redis;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.redis.RedisManager;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.configuration.api.IConfiguration;
import dev.endoy.configuration.api.ISection;

public final class RedisManagerFactory
{

    private RedisManagerFactory()
    {
    }

    public static RedisManager create()
    {
        final IConfiguration config = ConfigFiles.CONFIG.getConfig();
        final ISection section = config.getSection( "multi-proxy.redis" );

        if ( section.getBoolean( "clustered" ) )
        {
            BuX.getLogger().info( "Clustered uri was set to true, using ClusteredRedisManager." );
            return new ClusteredRedisManager( section );
        }

        BuX.getLogger().info( "Clustered uri was set to false, using StandaloneRedisManager." );
        return new StandardRedisManager( section );
    }
}
