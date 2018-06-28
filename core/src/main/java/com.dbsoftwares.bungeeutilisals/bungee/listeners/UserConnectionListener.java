package com.dbsoftwares.bungeeutilisals.bungee.listeners;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserPreLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.user.BUser;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import java.util.Optional;

public class UserConnectionListener implements Listener {

    @EventHandler
    public void onConnect(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        UserPreLoadEvent userPreLoadEvent = new UserPreLoadEvent(player, player.getAddress().getAddress());
        BungeeUtilisals.getApi().getEventLoader().launchEvent(userPreLoadEvent);

        if (userPreLoadEvent.isCancelled()) {
            BUCore.getApi().getDebugger().debug("Did not load user %s, UserPreLoadEvent got cancelled!", player.getName());
            return;
        }

        BUser user = new BUser();
        user.load(userPreLoadEvent);
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Optional<User> optional = BungeeUtilisals.getApi().getUser(player);

        if (!optional.isPresent()) {
            return;
        }
        User user = optional.get();
        user.unload();
    }
}
