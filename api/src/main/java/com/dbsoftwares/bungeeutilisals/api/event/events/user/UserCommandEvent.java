package com.dbsoftwares.bungeeutilisals.api.event.events.user;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.interfaces.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Getter;
import lombok.Setter;

/**
 * This event is being executed upon User Command execute.
 */
public class UserCommandEvent extends AbstractEvent implements Cancellable {

    @Getter @Setter User user;
    @Getter @Setter String command;
    boolean cancelled = false;

    public UserCommandEvent(User user, String command) {
        this.user = user;
        this.command = command;
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
