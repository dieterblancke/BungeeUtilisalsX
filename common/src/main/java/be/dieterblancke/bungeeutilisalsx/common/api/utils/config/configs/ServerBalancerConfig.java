package be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.configuration.api.ISection;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
public class ServerBalancerConfig extends Config
{

    private final List<ServerBalancerGroup> balancerGroups = new ArrayList<>();

    public ServerBalancerConfig( final String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        this.balancerGroups.clear();
    }

    @Override
    protected void setup()
    {
        if ( config == null || !isEnabled() )
        {
            return;
        }

        balancerGroups.clear();

        for ( ISection section : config.getSectionList( "balancers" ) )
        {
            ServerGroup group = ConfigFiles.SERVERGROUPS.getServer( section.getString( "group" ) ).orElse( null );

            if ( group == null )
            {
                continue;
            }
            ServerBalancingMethod method = Utils.valueOfOr( section.getString( "method" ), ServerBalancingMethod.LEAST_PLAYERS );
            ISection commandSection = section.getSection( "command" );
            ISection pingerSection = section.getSection( "pinger" );
            ServerBalancerGroupPinger pinger = new ServerBalancerGroupPinger(
                    pingerSection.getInteger( "delay" ),
                    pingerSection.getInteger( "max-attempts" ),
                    pingerSection.getInteger( "cooldown" ),
                    pingerSection.getStringList( "motd-filter" )
                            .stream()
                            .map( Pattern::compile )
                            .collect( Collectors.toList() )
            );

            balancerGroups.add( new ServerBalancerGroup( group, method, commandSection, pinger ) );
        }
    }

    public Optional<ServerBalancerGroup> getServerBalancerGroupFor( final String serverName )
    {
        return balancerGroups
                .stream()
                .filter( it -> it.getServerGroup().isInGroup( serverName ) )
                .findFirst();
    }

    public enum ServerBalancingMethod
    {
        RANDOM, LEAST_PLAYERS, FIRST_NON_FULL, MOST_PLAYERS;
    }

    @Value
    public static class ServerBalancerGroup
    {
        ServerGroup serverGroup;
        ServerBalancingMethod method;
        ISection commandSection;
        ServerBalancerGroupPinger pinger;
    }

    @Value
    public static class ServerBalancerGroupPinger
    {
        int delay;
        int maxAttempts;
        int cooldown;
        List<Pattern> motdFilters;
    }
}