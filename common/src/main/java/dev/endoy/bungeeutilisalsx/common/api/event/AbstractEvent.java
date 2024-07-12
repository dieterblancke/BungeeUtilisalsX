package dev.endoy.bungeeutilisalsx.common.api.event;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.IBuXApi;
import dev.endoy.bungeeutilisalsx.common.api.event.event.BUEvent;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class AbstractEvent implements BUEvent
{

    @Override
    public IBuXApi getApi()
    {
        return BuX.getApi();
    }
}