package com.dbsoftwares.bungeeutilisals.api.event.events.user;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This event gets called if an User was successfully saved & logged out.
 */
@AllArgsConstructor
public class UserUnloadEvent extends AbstractEvent {

    @Getter private User user;

}