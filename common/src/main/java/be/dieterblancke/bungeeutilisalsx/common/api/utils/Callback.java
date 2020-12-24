package be.dieterblancke.bungeeutilisalsx.common.api.utils;

public interface Callback<T>
{

    void done( T value, Throwable throwable );

}
