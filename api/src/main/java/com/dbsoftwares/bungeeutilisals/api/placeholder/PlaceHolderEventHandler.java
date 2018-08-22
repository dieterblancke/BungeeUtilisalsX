package com.dbsoftwares.bungeeutilisals.api.placeholder;

import com.dbsoftwares.bungeeutilisals.api.placeholder.event.PlaceHolderEvent;

public abstract class PlaceHolderEventHandler {

    public abstract String getReplacement(PlaceHolderEvent event);

}
