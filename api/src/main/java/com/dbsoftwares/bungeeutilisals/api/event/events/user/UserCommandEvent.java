package com.dbsoftwares.bungeeutilisals.api.event.events.user;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.event.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Getter;
import lombok.Setter;

/**
 * This event is being executed upon User Command execute.
 */
public class UserCommandEvent extends AbstractEvent implements Cancellable {

    @Getter @Setter User user;
    @Getter @Setter String command;
    @Getter
    @Setter
    private boolean cancelled = false;

    public UserCommandEvent(User user, String command) {
        this.user = user;
        this.command = command;
    }

    public String getActualCommand() {
        return command.split(" ")[0].toLowerCase();
    }
}
