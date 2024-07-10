package dev.endoy.bungeeutilisalsx.common.event;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.event.BUEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.event.IEventHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@RequiredArgsConstructor
public class EventHandler<T extends BUEvent> implements IEventHandler<T>
{

    private final EventLoader eventloader;
    @Getter
    private final Class<T> eventClass;
    @Getter
    private final Method executor;
    @Getter
    private final Object executorInstance;
    private final boolean executeIfCancelled;
    @Getter
    private final int priority;

    private final AtomicInteger uses = new AtomicInteger( 0 );

    @Override
    public void unregister()
    {
        eventloader.unregister( this );
    }

    @Override
    public int getUsedAmount()
    {
        return uses.get();
    }

    @Override
    public boolean executeIfCancelled()
    {
        return executeIfCancelled;
    }

    void handle( BUEvent event )
    {
        try
        {
            T castedEvent = (T) event;

            executor.invoke( executorInstance, castedEvent );
            uses.getAndIncrement();
        }
        catch ( Exception e )
        {
            BuX.getLogger().log(
                    Level.SEVERE,
                    "Could not handle event in " + executor.getClass().getName() + ": " + eventClass.getSimpleName() + ": ",
                    e
            );
        }
    }

    @Override
    public String toString()
    {
        return "EventHandler for " + eventClass.getSimpleName() + ". Used Amount: " + uses + ", ingore cancelled: " + executeIfCancelled + ", priority: " + priority + ".";
    }
}