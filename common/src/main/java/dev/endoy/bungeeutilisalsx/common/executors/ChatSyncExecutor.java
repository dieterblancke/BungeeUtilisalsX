package dev.endoy.bungeeutilisalsx.common.executors;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Event;
import dev.endoy.bungeeutilisalsx.common.api.event.event.EventExecutor;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Priority;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.ChatSyncJob;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.configs.ChatSyncConfig.ChatSyncedServer;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

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
                    MessagePlaceholders.create()
                            .append( "message", event.getMessage() )
            );

            BuX.getInstance().getJobManager().executeJob( new ChatSyncJob(
                    chatSyncedServer.serverGroup().getName(),
                    chatSyncedServer.forceFormat() ? null : user.getServerName(),
                    message
            ) );
        } );
    }
}
