package com.dbsoftwares.bungeeutilisals.api.event.events;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class UserLoadEvent extends AbstractEvent {

    @Getter private User user;

}