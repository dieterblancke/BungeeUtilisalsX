package be.dieterblancke.bungeeutilisalsx.common.api.pluginsupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface PluginSupport
{

    List<PluginSupport> PLUGIN_SUPPORTS = new ArrayList<>();

    static void registerPluginSupport( final Class<? extends PluginSupport> pluginSupportClass )
    {
        try
        {
            PluginSupport pluginSupport = pluginSupportClass.getConstructor().newInstance();

            if ( pluginSupport.isEnabled() )
            {
                PLUGIN_SUPPORTS.add( pluginSupport );
                pluginSupport.registerPluginSupport();
            }
        }
        catch ( Throwable ignored )
        {
        }
    }

    @SuppressWarnings( "unchecked" )
    static <T> Optional<T> getPluginSupport( final Class<T> clazz )
    {
        return PLUGIN_SUPPORTS.stream()
                .filter( pluginSupport -> pluginSupport.getClass().equals( clazz ) )
                .map( it -> (T) it )
                .findFirst();
    }

    boolean isEnabled();

    void registerPluginSupport();
}
