package com.dbsoftwares.bungeeutilisals.api.event.events.user;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.interfaces.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Getter;
import lombok.Setter;

/**
 * This event will be executed upon User Chat. UserChatPreExecuteEvent is being executed AFTER this event.
 */
public class UserChatEvent extends AbstractEvent implements Cancellable {

    @Getter @Setter User user;
    @Getter @Setter String message;
    boolean cancelled = false;

    public UserChatEvent(User user, String message) {
        this.user = user;
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
