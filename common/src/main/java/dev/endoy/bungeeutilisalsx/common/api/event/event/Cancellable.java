package dev.endoy.bungeeutilisalsx.common.api.event.event;

public interface Cancellable
{

    /**
     * @return True if the event is cancelled, false if not.
     */
    boolean isCancelled();

    /**
     * @param cancelled True if you want to cancel the event, false if not.
     */
    void setCancelled( boolean cancelled );

}