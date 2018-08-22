package com.dbsoftwares.bungeeutilisals.api.placeholder.event;

import com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders.PlaceHolder;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Getter;

import java.util.List;

@Getter
public class InputPlaceHolderEvent extends PlaceHolderEvent {

    private String argument;

    public InputPlaceHolderEvent(User user, PlaceHolder placeHolder, String message, String argument) {
        super(user, placeHolder, message);

        this.argument = argument;
    }
}