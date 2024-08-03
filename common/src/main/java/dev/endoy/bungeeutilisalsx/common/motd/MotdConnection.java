package dev.endoy.bungeeutilisalsx.common.motd;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;

public abstract class MotdConnection
{

    private String name;

    public abstract int getVersion();

    public abstract InetSocketAddress getRemoteIp();

    public abstract InetSocketAddress getVirtualHost();

    @SneakyThrows
    public String getName()
    {
        if ( name == null )
        {
            this.name = BuX.getApi().getStorageManager().getDao().getUserDao()
                .getUsersOnIP( Utils.getIP( this.getRemoteIp() ) )
                .get()
                .stream()
                .findFirst()
                .orElse( null );
        }

        return this.name;
    }
}
