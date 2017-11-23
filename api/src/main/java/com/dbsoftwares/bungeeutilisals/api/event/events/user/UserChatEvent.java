package com.dbsoftwares.bungeeutilisals.api.event.events.user;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import lombok.Getter;
import lombok.Setter;

/**
 * This event will be executed upon User Chat. UserChatPreExecuteEvent is being executed AFTER this event.
 */
public class UserChatEvent extends AbstractEvent implements Cancellable {

    @Getter @Setter User user;
    @Getter @Setter String message;
    Boolean cancelled = false;

    public UserChatEvent(User user, String message) {
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
