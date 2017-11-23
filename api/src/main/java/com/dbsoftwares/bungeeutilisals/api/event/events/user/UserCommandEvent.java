package com.dbsoftwares.bungeeutilisals.api.event.events.user;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import lombok.Getter;
import lombok.Setter;

/**
 * This event is being executed upon User Command execute.
 */
public class UserCommandEvent extends AbstractEvent implements Cancellable {

    @Getter @Setter User user;
    @Getter @Setter String command;
    Boolean cancelled = false;

    public UserCommandEvent(User user, String command) {
        this.user = user;
        this.command = command;
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
