package com.dbsoftwares.bungeeutilisals.bungee.executors;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatPreExecuteEvent;
import com.dbsoftwares.bungeeutilisals.api.manager.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;

public class UserChatExecutor {

    IChatManager manager;

    public UserChatExecutor(IChatManager manager) {
        this.manager = manager;
    }

    public void onUnicodeReplace(UserChatPreExecuteEvent event) {
        String message = event.getMessage();
        IConfiguration config = BungeeUtilisals.getConfiguration(FileLocation.UTFSYMBOLS);

        if (config.getBoolean("fancychat.enabled") && event.getUser().getParent().hasPermission(config.getString("fancychat.permission"))) {
            event.setMessage(BungeeUtilisals.getApi().getChatManager().fancyFont(message));
        }
    }

    public void onUnicodeSymbol(UserChatEvent event) {
        IConfiguration config = BungeeUtilisals.getConfiguration(FileLocation.UTFSYMBOLS);

        if (config.getBoolean("symbols.enabled") && event.getUser().getParent().hasPermission(config.getString("symbols.permission"))) {
            event.setMessage(event.getApi().getChatManager().replaceSymbols(event.getMessage()));
        }
    }

    public void onSwearChat(UserChatEvent event) {
        User user = event.getUser();
        String message = event.getMessage();
        IConfiguration config = BungeeUtilisals.getConfiguration(FileLocation.ANTISWEAR);

        if (manager.checkForSwear(event.getUser(), message)) {
            BUCore.getApi().getDebugger().debug("%s failed swearing, message: %s", user.getName(), message);

            if (config.getBoolean("cancel")) {
                event.setCancelled(true);
            } else {
                event.setMessage(manager.replaceSwearWords(user, message, config.getString("replace")));
            }
            user.sendLangMessage("chat-protection.swear");
        }
    }

    public void onCapsChat(UserChatEvent event) {
        User user = event.getUser();
        String message = event.getMessage();
        IConfiguration config = BungeeUtilisals.getConfiguration(FileLocation.ANTICAPS);

        if (manager.checkForCaps(user, message)) {
            BUCore.getApi().getDebugger().debug("%s failed using caps, message: %s", user.getName(), message);

            if (config.getBoolean("cancel")) {
                event.setCancelled(true);
            } else {
                event.setMessage(event.getMessage().toLowerCase());
            }
            user.sendLangMessage("chat-protection.caps");
        }
    }

    public void onSpamChat(UserChatEvent event) {
        User user = event.getUser();

        if (manager.checkForSpam(user)) {
            event.setCancelled(true);

            user.sendLangMessage("chat-protection.spam", "%time%", user.getCooldowns().getLeftTime("CHATSPAM"));
        }
    }

    public void onAdChat(UserChatEvent event) {
        User user = event.getUser();
        String message = event.getMessage();

        if (manager.checkForAdvertisement(user, message)) {
            BUCore.getApi().getDebugger().debug("%s failed advertising, message: %s", user.getName(), message);
            event.setCancelled(true);

            user.sendLangMessage("chat-protection.advertise");
        }
    }
}