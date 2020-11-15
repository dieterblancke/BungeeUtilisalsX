package com.dbsoftwares.bungeeutilisalsx.common.api.utils;

public interface ProgressableCallback<T>
{

    void progress( T value );

    void done( T value, Throwable throwable );

}
