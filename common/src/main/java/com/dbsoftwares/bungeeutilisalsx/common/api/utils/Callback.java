package com.dbsoftwares.bungeeutilisalsx.common.api.utils;

public interface Callback<T>
{

    void done( T value, Throwable throwable );

}
