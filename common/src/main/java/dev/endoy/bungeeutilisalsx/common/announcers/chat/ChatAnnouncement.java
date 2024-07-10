package dev.endoy.bungeeutilisalsx.common.announcers.chat;

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
public class ChatAnnouncement extends Announcement
{

    private boolean prefix;
    private String languagePath;
    private List<String> messages;

    public ChatAnnouncement( boolean prefix, String languagePath, ServerGroup serverGroup, String receivePermission )
    {
        super( serverGroup, receivePermission );

        this.prefix = prefix;
        this.languagePath = languagePath;
    }

    public ChatAnnouncement( boolean prefix, List<String> messages, ServerGroup serverGroup, String receivePermission )
    {
        super( serverGroup, receivePermission );

        this.prefix = prefix;
        this.messages = messages;
    }

    @Override
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

    private void send( Stream<User> stream )
    {
        stream.forEach( user ->
        {
            final IConfiguration config = user.getLanguageConfig().getConfig();

            if ( languagePath != null )
            {
                user.sendLangMessage( languagePath );
            }
            else
            {
                for ( String message : messages )
                {
                    if ( prefix )
                    {
                        message = config.getString( "prefix" ) + message;
                    }
                    user.sendMessage( Utils.format( user, message ) );
                }
            }
        } );
    }
}