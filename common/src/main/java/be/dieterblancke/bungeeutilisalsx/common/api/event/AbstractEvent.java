package be.dieterblancke.bungeeutilisalsx.common.api.event;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.IBuXApi;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.BUEvent;
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