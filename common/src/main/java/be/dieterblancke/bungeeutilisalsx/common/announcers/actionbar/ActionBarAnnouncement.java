package be.dieterblancke.bungeeutilisalsx.common.announcers.actionbar;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcement;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@EqualsAndHashCode( callSuper = false )
public class ActionBarAnnouncement extends Announcement
{

    private boolean language;
    private int time;
    private String message;
    private ScheduledFuture task;

    public ActionBarAnnouncement( boolean language, int time, String message, ServerGroup serverGroup, String receivePermission )
    {
        super( serverGroup, receivePermission );

        this.language = language;
        this.time = time;
        this.message = message;
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

        if ( time > 1 )
        {
            task = BuX.getInstance().getScheduler().runTaskRepeating( 1, 1, TimeUnit.SECONDS, new Runnable()
            {

                private int count = 1;

                @Override
                public void run()
                {
                    count++;

                    if ( count > time )
                    {
                        task.cancel( true );
                        return;
                    }
                    if ( serverGroup.isGlobal() )
                    {
                        send( filter( BuX.getApi().getUsers().stream() ) );
                    }
                    else
                    {
                        serverGroup.getServers().forEach( server -> send( filter( server.getUsers().stream() ) ) );
                    }
                }
            } );
        }
    }

    private void send( Stream<User> stream )
    {
        stream.forEach( player ->
        {
            String bar = message;

            if ( language )
            {
                bar = BuX.getApi().getLanguageManager().getLanguageConfiguration(
                        BuX.getInstance().getName(), player
                ).getConfig().getString( message );
            }

            player.sendActionBar( bar );
        } );
    }
}