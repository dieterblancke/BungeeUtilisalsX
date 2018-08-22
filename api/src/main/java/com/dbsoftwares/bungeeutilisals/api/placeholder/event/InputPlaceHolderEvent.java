package com.dbsoftwares.bungeeutilisals.api.placeholder.event;

import com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders.PlaceHolder;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Getter;

@Getter
public class InputPlaceHolderEvent extends PlaceHolderEvent {

    public InputPlaceHolderEvent(User user, PlaceHolder placeHolder, String message) {
        super(user, placeHolder, message);
    }
}