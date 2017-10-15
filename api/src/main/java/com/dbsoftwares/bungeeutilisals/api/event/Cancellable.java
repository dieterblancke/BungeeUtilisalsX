package com.dbsoftwares.bungeeutilisals.api.event;

public interface Cancellable {

    /**
     * @param cancelled True if you want to cancel the event, false if not.
     */
    void setCancelled(Boolean cancelled);

    /**
     * @return True if the event is cancelled, false if not.
     */
    Boolean isCancelled();
}