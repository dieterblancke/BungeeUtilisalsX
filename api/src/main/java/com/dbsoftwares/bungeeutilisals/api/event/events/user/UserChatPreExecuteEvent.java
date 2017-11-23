package com.dbsoftwares.bungeeutilisals.api.event.events.user;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import lombok.Getter;
import lombok.Setter;

/**
 * Last BungeeUtilisals event before actually sending the message (this event comes AFTER UserChatEvent).
 * This event was mainly made for the FancyChat messages.
 */
public class UserChatPreExecuteEvent extends AbstractEvent implements Cancellable {

    @Getter @Setter User user;
    @Getter @Setter String message;
    Boolean cancelled = false;

    public UserChatPreExecuteEvent(User user, String message) {
        this.user = user;
        this.message = message;
    }

    @Override
    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Boolean isCancelled() {
        return cancelled;
    }
}
