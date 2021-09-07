package be.dieterblancke.bungeeutilisalsx.common.api.pluginsupport;

import java.util.ArrayList;
import java.util.List;

public interface PluginSupport
{

    List<PluginSupport> PLUGIN_SUPPORTS = new ArrayList<>();

    static void registerPluginSupport( final PluginSupport pluginSupport )
    {
        PLUGIN_SUPPORTS.add( pluginSupport );

        if ( pluginSupport.isEnabled() )
        {
            pluginSupport.registerPluginSupport();
        }
    }

    @SuppressWarnings( "unchecked" )
    static <T> T getPluginSupport( final Class<T> clazz )
    {
        return (T) PLUGIN_SUPPORTS.stream()
                .filter( pluginSupport -> pluginSupport.getClass().equals( clazz ) )
                .findFirst()
                .orElse( null );
    }

    boolean isEnabled();

    void registerPluginSupport();
}
