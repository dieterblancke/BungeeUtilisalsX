package dev.endoy.bungeeutilisalsx.common.announcers.tab;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.announcer.Announcement;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import dev.endoy.configuration.api.IConfiguration;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@EqualsAndHashCode( callSuper = false )
public class TabAnnouncement extends Announcement
{

    private boolean language;
    private List<String> header;
    private List<String> footer;

    public TabAnnouncement( boolean language, List<String> header, List<String> footer, ServerGroup serverGroup, String receivePermission )
    {
        super( serverGroup, receivePermission );

        this.language = language;
        this.header = header;
        this.footer = footer;
    }

    public void send()
    {
        if ( serverGroup.isGlobal() )
        {
            send( filter( BuX.getApi().getUsers().stream() ) );
        }
        else
        {
            serverGroup.getServers().forEach( server -> send( filter( server.getUsers().stream() ) ) );
        }
    }

    private void send( final Stream<User> stream )
    {
        stream.forEach( user ->
        {
            final IConfiguration config = user.getLanguageConfig().getConfig();

            final List<String> headers = language ? config.getStringList( this.header.get( 0 ) ) : this.header;
            final List<String> footers = language ? config.getStringList( this.footer.get( 0 ) ) : this.footer;

            user.setTabHeader(
                    Utils.format( user, headers ),
                    Utils.format( user, footers )
            );
        } );
    }
}