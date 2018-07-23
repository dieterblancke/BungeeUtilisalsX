package com.dbsoftwares.bungeeutilisals.executors;

import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.event.Priority;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatPreExecuteEvent;
import com.dbsoftwares.bungeeutilisals.api.manager.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;

public class UserChatExecutor implements EventExecutor {

    IChatManager manager;

    public UserChatExecutor(IChatManager manager) {
        this.manager = manager;
    }

    @Event(priority = Priority.LOW, executeIfCancelled = false)
    public void onUnicodeReplace(UserChatPreExecuteEvent event) {
        String message = event.getMessage();
        IConfiguration config = FileLocation.UTFSYMBOLS.getConfiguration();

        if (config.getBoolean("fancychat.enabled") && event.getUser().getParent().hasPermission(config.getString("fancychat.permission"))) {
            event.setMessage(BungeeUtilisals.getApi().getChatManager().fancyFont(message));
        }
    }

    @Event(priority = Priority.LOW, executeIfCancelled = false)
    public void onUnicodeSymbol(UserChatEvent event) {
        IConfiguration config = FileLocation.UTFSYMBOLS.getConfiguration();

        if (config.getBoolean("symbols.enabled") && event.getUser().getParent().hasPermission(config.getString("symbols.permission"))) {
            event.setMessage(event.getApi().getChatManager().replaceSymbols(event.getMessage()));
        }
    }

    @Event(priority = Priority.HIGH, executeIfCancelled = false)
    public void onSwearChat(UserChatEvent event) {
        User user = event.getUser();
        String message = event.getMessage();
        IConfiguration config = FileLocation.ANTISWEAR.getConfiguration();

        if (manager.checkForSwear(event.getUser(), message)) {
            if (config.getBoolean("cancel")) {
                event.setCancelled(true);
            } else {
                event.setMessage(manager.replaceSwearWords(user, message, config.getString("replace")));
            }
            user.sendLangMessage("chat-protection.swear");
        }
    }

    @Event(priority = Priority.HIGH, executeIfCancelled = false)
    public void onCapsChat(UserChatEvent event) {
        User user = event.getUser();
        String message = event.getMessage();
        IConfiguration config = FileLocation.ANTICAPS.getConfiguration();

        if (manager.checkForCaps(user, message)) {
            if (config.getBoolean("cancel")) {
                event.setCancelled(true);
            } else {
                event.setMessage(event.getMessage().toLowerCase());
            }
            user.sendLangMessage("chat-protection.caps");
        }
    }

    @Event(priority = Priority.HIGH, executeIfCancelled = false)
    public void onSpamChat(UserChatEvent event) {
        User user = event.getUser();

        if (manager.checkForSpam(user)) {
            event.setCancelled(true);

            user.sendLangMessage("chat-protection.spam", "%time%", user.getCooldowns().getLeftTime("CHATSPAM") / 1000);
        }
    }

    @Event(priority = Priority.HIGH, executeIfCancelled = false)
    public void onAdChat(UserChatEvent event) {
        User user = event.getUser();
        String message = event.getMessage();

        if (manager.checkForAdvertisement(user, message)) {
            event.setCancelled(true);

            user.sendLangMessage("chat-protection.advertise");
        }
    }
}