package com.dbsoftwares.bungeeutilisals.importer;

/*
 * Created by DBSoftwares on 27/08/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import net.md_5.bungee.api.Callback;

public interface ImporterCallback<T> extends Callback<T> {

    void onStatusUpdate(final T status);

}
