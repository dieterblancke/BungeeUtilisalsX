package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectedEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            sentMotds.add( motd.getUuid() );
        } );
    }
}
