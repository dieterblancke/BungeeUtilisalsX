package com.dbsoftwares.bungeeutilisals.bungee.listeners;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.event.events.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.UserCommandEvent;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Optional;

public class UserChatListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        BUAPI api = BungeeUtilisals.getApi();
        Optional<User> optional = api.getUser(((CommandSender) event.getSender()).getName());

        if (!optional.isPresent()) {
            return;
        }
        User user = optional.get();

        if (event.isCommand()) {
            UserCommandEvent commandEvent = new UserCommandEvent(user, event.getMessage());
            api.getEventLoader().launchEvent(commandEvent);

            if (commandEvent.isCancelled()) {
                event.setCancelled(true);
            }
            event.setMessage(commandEvent.getCommand());
        } else {
            UserChatEvent chatEvent = new UserChatEvent(user, event.getMessage());
            System.out.println("Eerste message: " + chatEvent.getMessage());
            api.getEventLoader().launchEvent(chatEvent);

            if (chatEvent.isCancelled()) {
                event.setCancelled(true);
            }

            System.out.println("Tweede message: " + chatEvent.getMessage());
            event.setMessage(chatEvent.getMessage());
        }
    }
}