package be.dieterblancke.bungeeutilisalsx.common.api.announcer;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import lombok.Data;

import java.util.stream.Stream;

@Data
public abstract class Announcement implements IAnnouncement
{

    protected ServerGroup serverGroup;
    protected String receivePermission;

    protected Announcement( ServerGroup serverGroup, String receivePermission )
    {
        this.serverGroup = serverGroup;
        this.receivePermission = receivePermission;
    }

    public abstract void send();

    protected Stream<User> filter( final Stream<User> stream )
    {
        return receivePermission.isEmpty()
                ? stream
                : stream.filter( user -> user.hasPermission( receivePermission ) || user.hasPermission( "bungeeutilisals.*" ) || user.hasPermission( "*" ) );
    }

    public void clear()
    {
    }
}