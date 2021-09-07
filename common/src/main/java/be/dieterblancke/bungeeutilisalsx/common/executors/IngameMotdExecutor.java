package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.announcers.bossbar.BossBarMessage;
import be.dieterblancke.bungeeutilisalsx.common.announcers.title.TitleMessage;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.IBossBar;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectedEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.IConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class IngameMotdExecutor implements EventExecutor
{

    private static final String STORAGE_KEY = "SENT_INGAME_MOTDS";

    @Event
    public void onServerSwitch( final UserServerConnectedEvent event )
    {
        if ( !ConfigFiles.INGAME_MOTD_CONFIG.isEnabled() )
        {
            return;
        }
        final User user = event.getUser();
        final UserStorage storage = user.getStorage();

        if ( !storage.hasData( STORAGE_KEY ) )
        {
            storage.setData( STORAGE_KEY, new ArrayList<>() );
        }

        final List<UUID> sentMotds = storage.getData( STORAGE_KEY );

        ConfigFiles.INGAME_MOTD_CONFIG.getApplicableMotds( event.getTarget() ).forEach( motd ->
        {
            if ( sentMotds.contains( motd.getUuid() ) && ( motd.getServer() == null || motd.isOncePerSession() ) )
            {
                return;
            }
            if ( motd.hasReceivePermission() && !user.hasPermission( motd.getReceivePermission() ) )
            {
                return;
            }

            if ( motd.isLanguage() )
            {
                if ( motd.getMessage().size() > 0 )
                {
                    user.sendLangMessage( motd.getMessage().get( 0 ) );
                }
            }
            else
            {
                for ( String line : motd.getMessage() )
                {
                    user.sendRawColorMessage( line );
                }
            }
            if ( motd.hasActionBar() )
            {
                sendActionBar( user, motd.isLanguage(), motd.getActionBar() );
            }
            if ( motd.hasBossBar() )
            {
                sendBossBar( user, motd.isLanguage(), motd.getBossBar() );
            }
            if ( motd.hasTitle() )
            {
                sendTitle( user, motd.isLanguage(), motd.getTitle() );
            }
            sentMotds.add( motd.getUuid() );
        } );
    }

    private void sendActionBar( final User user, final boolean language, final String message )
    {
        user.sendActionBar( locateMessage( language, user.getLanguageConfig().getConfig(), message ) );
    }

    private void sendBossBar( final User user, final boolean language, final BossBarMessage message )
    {
        final IBossBar bar = BuX.getApi().createBossBar();
        bar.setMessage( Utils.format(
                user,
                locateMessage( language, user.getLanguageConfig().getConfig(), message.getText() )
        ) );
        bar.setColor( message.getColor() );
        bar.setProgress( message.getProgress() );
        bar.setStyle( message.getStyle() );
        bar.addUser( user );
    }

    private void sendTitle( final User user, final boolean language, final TitleMessage message )
    {
        user.sendTitle(
                locateMessage( language, user.getLanguageConfig().getConfig(), message.getTitle() ),
                locateMessage( language, user.getLanguageConfig().getConfig(), message.getSubtitle() ),
                message.getFadeIn(),
                message.getStay(),
                message.getFadeOut()
        );
    }

    private String locateMessage( final boolean language, final IConfiguration config, final String str )
    {
        if ( language )
        {
            if ( config.exists( str ) )
            {
                if ( config.isString( str ) )
                {
                    return config.getString( str );
                }
                else if ( config.isList( str ) )
                {
                    return config.getStringList( str ).stream().collect( Collectors.joining( System.lineSeparator() ) );
                }
            }
        }
        return str;
    }
}
