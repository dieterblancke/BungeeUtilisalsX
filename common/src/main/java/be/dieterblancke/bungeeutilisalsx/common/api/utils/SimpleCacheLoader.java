package be.dieterblancke.bungeeutilisalsx.common.api.utils;

public interface SimpleCacheLoader<K, V>
{

    V load( K k ) throws Exception;

}
