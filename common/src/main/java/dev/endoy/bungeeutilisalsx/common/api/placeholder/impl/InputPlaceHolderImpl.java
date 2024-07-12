package dev.endoy.bungeeutilisalsx.common.api.placeholder.impl;

import dev.endoy.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.event.handler.InputPlaceHolderEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class InputPlaceHolderImpl extends InputPlaceHolderEventHandler
{

    private final boolean requiresUser;
    private final String prefix;

    public void register()
    {
        PlaceHolderAPI.addPlaceHolder( requiresUser, prefix, this );
    }
}
