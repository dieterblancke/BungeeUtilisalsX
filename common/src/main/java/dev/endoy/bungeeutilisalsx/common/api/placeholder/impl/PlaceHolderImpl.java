package dev.endoy.bungeeutilisalsx.common.api.placeholder.impl;

import dev.endoy.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.event.handler.PlaceHolderEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class PlaceHolderImpl implements PlaceHolderEventHandler
{

    private final String placeHolder;
    private final boolean requiresUser;

    public void register()
    {
        PlaceHolderAPI.addPlaceHolder( placeHolder, requiresUser, this );
    }
}
