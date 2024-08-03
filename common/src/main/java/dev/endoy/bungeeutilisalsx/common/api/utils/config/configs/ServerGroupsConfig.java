package dev.endoy.bungeeutilisalsx.common.api.utils.config.configs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.Config;
import dev.endoy.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import dev.endoy.configuration.api.ISection;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

public class ServerGroupsConfig extends Config
{

    @Getter
    private final Map<String, ServerGroup> servers = Maps.newHashMap();

    public ServerGroupsConfig( String location )
    {
        super( location );
    }

    public Optional<ServerGroup> getServer( final String server )
    {
        return Optional.ofNullable( servers.get( server ) );
    }

    @Override
    public void purge()
    {
        servers.clear();
    }

    @Override
    public void setup()
    {
        if ( config == null )
        {
            return;
        }

        for ( ISection group : config.getSectionList( "groups" ) )
        {
            String name = group.getString( "name" );
            boolean dynamic = group.exists( "dynamic" ) ? group.getBoolean( "dynamic" ) : false;

            if ( group.isList( "servers" ) )
            {
                servers.put( name, new ServerGroup( name, false, dynamic, group.getStringList( "servers" ) ) );
            }
            else
            {
                servers.put( name, new ServerGroup( name, true, dynamic, Lists.newArrayList() ) );
            }
        }

        for ( IProxyServer server : BuX.getInstance().serverOperations().getServers() )
        {
            if ( !servers.containsKey( server.getName() ) )
            {
                servers.put(
                    server.getName(),
                    new ServerGroup( server.getName(), false, false, Lists.newArrayList( server.getName() ) )
                );
            }
        }
    }
}
