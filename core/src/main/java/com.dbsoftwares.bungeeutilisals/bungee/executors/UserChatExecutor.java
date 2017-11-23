package com.dbsoftwares.bungeeutilisals.bungee.executors;

import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatPreExecuteEvent;
import com.dbsoftwares.bungeeutilisals.api.manager.IChatManager;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.settings.Settings;

public class UserChatExecutor {

    IChatManager manager;

    public UserChatExecutor(IChatManager manager) {
        this.manager = manager;
    }

    public void onUnicodeReplace(UserChatPreExecuteEvent event) {
        String message = event.getMessage();
        Settings settings = BungeeUtilisals.getInstance().getSettings();

        if (settings.FANCYCHAT_ENABLED.get() && event.getUser().getParent().hasPermission(settings.FANCYCHAT_PERMISSION.get())) {
            event.setMessage(BungeeUtilisals.getApi().getChatManager().fancyFont(message));
        }
    }

    public void onUnicodeSymbol(UserChatEvent event) {
        Settings settings = BungeeUtilisals.getInstance().getSettings();

        if (settings.UTFSYMBOLS_ENABLED.get() && event.getUser().getParent().hasPermission(settings.UTFSYMBOLS_PERMISSION.get())) {
            event.setMessage(event.getApi().getChatManager().replaceSymbols(event.getMessage()));
        }
    }

    public void onSwearChat(UserChatEvent event) {
        String message = event.getMessage();
        Settings settings = BungeeUtilisals.getInstance().getSettings();

        if (manager.checkForSwear(event.getUser(), message)) {
            if (settings.ANTISWEAR_CANCEL.get()) {
                // TODO: send swear message.

                event.setCancelled(true);
            } else {
                // TODO: send swear message.

                event.setMessage(manager.replaceSwearWords(event.getUser(), message, settings.ANTISWEAR_REPLACEMENT.get()));
            }
        }
    }
}