package be.dieterblancke.bungeeutilisalsx.bungee.pluginsupports;

import be.dieterblancke.bungeeutilisalsx.bungee.Bootstrap;
import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserVanishUpdateJob;
import be.dieterblancke.bungeeutilisalsx.common.api.pluginsupport.PluginSupport;
import de.myzelyam.api.vanish.BungeePlayerHideEvent;
import de.myzelyam.api.vanish.BungeePlayerShowEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PremiumVanishPluginSupport implements PluginSupport
{

    @Override
    public boolean isEnabled()
    {
        return BuX.getInstance().proxyOperations().getPlugin( "PremiumVanish" ).isPresent();
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
