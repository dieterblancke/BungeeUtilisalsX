package be.dieterblancke.bungeeutilisalsx.common.api.utils;

public interface TriFunction<K1, K2, K3, R>
{
    R handle( K1 var1, K2 var2, K3 var3 );
}