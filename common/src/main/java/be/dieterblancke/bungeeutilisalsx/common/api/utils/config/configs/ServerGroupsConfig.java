package be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.configuration.api.ISection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

public class ServerGroupsConfig extends Config
{

    @Getter
    private final Map<String, ServerGroup> servers = Maps.newHashMap();

    public ServerGroupsConfig( String location )
    {
        super( location );
    }

    public ServerGroup getServer( final String server )
    {
        return servers.get( server );
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

            if ( group.isList( "servers" ) )
            {
                servers.put( name, new ServerGroup( name, false, group.getStringList( "servers" ) ) );
            }
            else
            {
                servers.put( name, new ServerGroup( name, true, Lists.newArrayList() ) );
            }
        }

        for ( IProxyServer server : BuX.getInstance().proxyOperations().getServers() )
        {
            if ( !servers.containsKey( server.getName() ) )
            {
                servers.put(
                        server.getName(),
                        new ServerGroup( server.getName(), false, Lists.newArrayList( server.getName() ) )
                );
            }
        }
    }
}
