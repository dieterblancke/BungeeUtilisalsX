package com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders;

/*
 * Created by DBSoftwares on 22/08/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler.PlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@EqualsAndHashCode
public abstract class PlaceHolder {

    protected String placeHolder;
    @Accessors(fluent = true)
    protected boolean requiresUser;
    protected PlaceHolderEventHandler eventHandler;

    public PlaceHolder(String placeHolder, boolean requiresUser, PlaceHolderEventHandler eventHandler) {
        this.placeHolder = placeHolder;
        this.requiresUser = requiresUser;
        this.eventHandler = eventHandler;
    }

    public boolean requiresUser() {
        return requiresUser;
    }

    public abstract String format(User user, String message);
}
