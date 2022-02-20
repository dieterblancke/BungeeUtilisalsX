package be.dieterblancke.bungeeutilisalsx.common.api.event.event;

import be.dieterblancke.bungeeutilisalsx.common.BuX;

public interface EventExecutor
{

    default void registerForEvents( final Class<? extends BUEvent>... eventClasses )
    {
        for ( Class<? extends BUEvent> eventClass : eventClasses )
        {
            BuX.getApi().getEventLoader().register( eventClass, this );
        }
    }

}