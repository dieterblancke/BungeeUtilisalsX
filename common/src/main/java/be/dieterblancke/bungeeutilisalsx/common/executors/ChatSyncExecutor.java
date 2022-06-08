package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Priority;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.ChatSyncJob;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.ChatSyncConfig.ChatSyncedServer;

import java.util.Optional;

public class ChatSyncExecutor implements EventExecutor
{

    @Event( priority = Priority.HIGHEST )
    public void onChat( final UserChatEvent event )
    {
        if ( event.isCancelled() || !ConfigFiles.CHAT_SYNC_CONFIG.isEnabled() )
        {
            return;
        }
        final User user = event.getUser();
        final Optional<ChatSyncedServer> optionalChatSyncedServer = ConfigFiles.CHAT_SYNC_CONFIG.getChatSyncedServer(
                user.getServerName()
        );

        optionalChatSyncedServer.ifPresent( chatSyncedServer ->
        {
            if ( chatSyncedServer.forceFormat() )
            {
                event.setCancelled( true );
            }

            final String message = Utils.replacePlaceHolders(
                    chatSyncedServer.format(),
                    str -> PlaceHolderAPI.formatMessage( user, str ),
                    null,
                    "{message}", event.getMessage()
            );

            BuX.getInstance().getJobManager().executeJob( new ChatSyncJob(
                    chatSyncedServer.serverGroup().getName(),
                    chatSyncedServer.forceFormat() ? null : user.getServerName(),
                    message
            ) );
        } );
    }
}
