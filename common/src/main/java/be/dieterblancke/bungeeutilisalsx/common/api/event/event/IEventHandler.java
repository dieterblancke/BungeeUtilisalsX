package be.dieterblancke.bungeeutilisalsx.common.api.event.event;

import java.lang.reflect.Method;

public interface IEventHandler<T extends BUEvent>
{

    /**
     * @return the event clas this handler listens to.
     */
    Class<T> getEventClass();

    /**
     * Stops this handler from listening to events.
     */
    void unregister();

    /**
     * Gets the event executor handling the event
     *
     * @return event executor handling the event
     */
    Method getExecutor();

    /**
     * Gets the amount this event handler has been called
     *
     * @return the amount of times this handler has been called
     */
    int getUsedAmount();

    /**
     * Gets whether the event should be executed when cancelled or not.
     *
     * @return True if it should ignore cancelled events, false if not.
     */
    boolean executeIfCancelled();

    /**
     * Gets the event priority. The lower the priority is, the earlier it will get executed.
     * Higher priority = later executed = last influence
     *
     * @return the priority of this event
     */
    int getPriority();
}