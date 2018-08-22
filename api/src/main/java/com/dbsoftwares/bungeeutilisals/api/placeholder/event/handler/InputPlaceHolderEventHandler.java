package com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler;

import com.dbsoftwares.bungeeutilisals.api.placeholder.event.InputPlaceHolderEvent;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.PlaceHolderEvent;

public abstract class InputPlaceHolderEventHandler extends PlaceHolderEventHandler {

    public abstract String getReplacement(InputPlaceHolderEvent event);

    @Override
    public String getReplacement(PlaceHolderEvent event) {
        return getReplacement((InputPlaceHolderEvent) event);
    }
}
