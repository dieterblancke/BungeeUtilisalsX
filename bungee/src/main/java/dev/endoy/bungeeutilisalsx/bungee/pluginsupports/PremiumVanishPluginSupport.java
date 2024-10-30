package dev.endoy.bungeeutilisalsx.bungee.pluginsupports;

import de.myzelyam.api.vanish.BungeePlayerHideEvent;
import de.myzelyam.api.vanish.BungeePlayerShowEvent;
import dev.endoy.bungeeutilisalsx.bungee.Bootstrap;
import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserVanishUpdateJob;
import dev.endoy.bungeeutilisalsx.common.api.pluginsupport.PluginSupport;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PremiumVanishPluginSupport implements PluginSupport
{

    @Override
    public boolean isEnabled()
    {
        return BuX.getInstance().serverOperations().getPlugin( "PremiumVanish" ).isPresent();
    }

    @Override
    public void registerPluginSupport()
    {
        ProxyServer.getInstance().getPluginManager().registerListener(
            Bootstrap.getInstance(),
            new PremiumVanishPluginSupportListener()
        );
    }

    public static class PremiumVanishPluginSupportListener implements Listener
    {

        @EventHandler
        public void onBungeePlayerShow( BungeePlayerShowEvent event )
        {
            BuX.getInstance().getJobManager().executeJob( new UserVanishUpdateJob(
                event.getPlayer().getUniqueId(),
                event.getPlayer().getName(),
                false
            ) );
        }

        @EventHandler
        public void onBungeePlayerHide( BungeePlayerHideEvent event )
        {
            BuX.getInstance().getJobManager().executeJob( new UserVanishUpdateJob(
                event.getPlayer().getUniqueId(),
                event.getPlayer().getName(),
                true
            ) );
        }
    }
}
