package com.dbsoftwares.bungeeutilisals.api.placeholder;


import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import lombok.Getter;

public class PlaceHolder {

    @Getter
    String placeHolder;
    boolean requiresUser;
    @Getter
    PlaceHolderEventHandler eventHandler;

    public PlaceHolder(String placeHolder, boolean requiresUser, PlaceHolderEventHandler eventHandler) {
        this.placeHolder = placeHolder;
        this.requiresUser = requiresUser;
        this.eventHandler = eventHandler;
    }

    public boolean requiresUser() {
        return requiresUser;
    }

    public String format(User user, String message) {
        if (placeHolder == null || !message.contains(placeHolder)) {
            return message;
        }
        PlaceHolderEvent event = new PlaceHolderEvent(user, this, message);
        String replace = eventHandler.getReplace(event);
        return message.replace(placeHolder, Utils.c(replace));
    }
}