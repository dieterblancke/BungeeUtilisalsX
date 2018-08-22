package com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders;


import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.PlaceHolderEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;

public class DefaultPlaceHolder extends PlaceHolder {

    public DefaultPlaceHolder(String placeHolder, boolean requiresUser, PlaceHolderEventHandler handler) {
        super(placeHolder, requiresUser, handler);
    }

    @Override
    public String format(User user, String message) {
        if (placeHolder == null || !message.contains(placeHolder)) {
            return message;
        }
        PlaceHolderEvent event = new PlaceHolderEvent(user, this, message);
        return message.replace(placeHolder, Utils.c(eventHandler.getReplacement(event)));
    }
}