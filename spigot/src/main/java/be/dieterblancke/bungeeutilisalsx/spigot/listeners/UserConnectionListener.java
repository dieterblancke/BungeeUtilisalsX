/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package be.dieterblancke.bungeeutilisalsx.spigot.listeners;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.spigot.user.SpigotUser;
import be.dieterblancke.bungeeutilisalsx.spigot.Bootstrap;
import be.dieterblancke.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class UserConnectionListener implements Listener
{

    @EventHandler
    public void onConnect( final PlayerLoginEvent event )
    {
        final SpigotUser user = new SpigotUser();

        user.load( event.getPlayer(), event.getAddress() );

        // TODO: remove, this is as a test =)
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                ( (BungeeUtilisalsX) BuX.getInstance() ).getGuiManager().openGui( event.getPlayer(), "friend", new String[0] );
            }
        }.runTaskLater( Bootstrap.getInstance(), 100 );
    }

    @EventHandler
    public void onDisconnect( final PlayerQuitEvent event )
    {
        final Player player = event.getPlayer();
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getName() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final User user = optional.get();
        user.unload();
    }
}
