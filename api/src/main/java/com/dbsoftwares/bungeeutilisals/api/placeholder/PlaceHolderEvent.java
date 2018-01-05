package com.dbsoftwares.bungeeutilisals.api.placeholder;

import com.dbsoftwares.bungeeutilisals.api.user.User;

public class PlaceHolderEvent {

    User user;
    PlaceHolder placeholder;
    String message;

    public PlaceHolderEvent(User user, PlaceHolder placeholder, String message) {
        this.user = user;
        this.placeholder = placeholder;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public PlaceHolder getPlaceHolder() {
        return placeholder;
    }
}