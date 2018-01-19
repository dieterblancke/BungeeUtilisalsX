package com.dbsoftwares.bungeeutilisals.api.event.events.user;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This event is being executed when a User has successfully been loaded in.
 */
@AllArgsConstructor
public class UserLoadEvent extends AbstractEvent {

    @Getter private User user;

}