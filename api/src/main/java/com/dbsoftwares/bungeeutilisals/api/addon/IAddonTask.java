package com.dbsoftwares.bungeeutilisals.api.addon;

public interface IAddonTask {

    int getId();

    Addon getOwner();

    Runnable getTask();

    void cancel();

}
