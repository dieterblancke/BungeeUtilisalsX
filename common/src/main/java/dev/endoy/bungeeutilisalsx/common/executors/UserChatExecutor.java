package dev.endoy.bungeeutilisalsx.common.executors;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Event;
import dev.endoy.bungeeutilisalsx.common.api.event.event.EventExecutor;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Priority;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorageKey;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.chat.ChatHelper;
import dev.endoy.bungeeutilisalsx.common.chat.ChatProtections;
import dev.endoy.bungeeutilisalsx.common.chat.protections.SwearValidationResult;
import dev.endoy.bungeeutilisalsx.common.commands.general.ChatLockCommandCall;
import dev.endoy.configuration.api.IConfiguration;

import java.util.List;
import java.util.function.Consumer;

public class UserChatExecutor implements EventExecutor
{

    @Event
    public void onChat( final UserChatEvent event )
    {
        if ( event.isCancelled() )
        {
            return;
        }
        final User user = event.getUser();
        final boolean isServerChatLocked = ChatLockCommandCall.lockedChatServers.contains( "ALL" )
            || ChatLockCommandCall.lockedChatServers.contains( user.getServerName() );

        if ( !user.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "chatlock.bypass" ) )
            && isServerChatLocked )
        {
            event.setCancelled( true );
            user.sendLangMessage( "general-commands.chatlock.onchat" );
        }
    }

    @Event( priority = Priority.HIGHEST, executeIfCancelled = false )
    public void onUnicodeReplace( UserChatEvent event )
    {
        final String message = event.getMessage();
        final IConfiguration config = ConfigFiles.UTFSYMBOLS.getConfig();

        if ( config.getBoolean( "fancychat.enabled" )
            && event.getUser().hasPermission( config.getString( "fancychat.permission" ) ) )
        {
            event.setMessage( ChatHelper.changeToFancyFont( message ) );
        }
    }

    @Event( priority = Priority.LOW, executeIfCancelled = false )
    public void onUnicodeSymbol( UserChatEvent event )
    {
        final IConfiguration config = ConfigFiles.UTFSYMBOLS.getConfig();

        if ( config.getBoolean( "symbols.enabled" ) && event.getUser().hasPermission( config.getString( "symbols.permission" ) ) )
        {
            event.setMessage( ChatHelper.replaceSymbols( event.getUser(), event.getMessage() ) );
        }
    }

    @Event( priority = Priority.HIGH, executeIfCancelled = false )
    public void onSwearChat( UserChatEvent event )
    {
        final User user = event.getUser();
        final String message = event.getMessage();
        final IConfiguration config = ConfigFiles.ANTISWEAR.getConfig();
        final SwearValidationResult swearValidationResult = ChatProtections.SWEAR_PROTECTION.validateMessage( user, message );

        if ( !swearValidationResult.isValid() )
        {
            if ( config.getBoolean( "cancel" ) )
            {
                event.setCancelled( true );
            }
            else
            {
                event.setMessage( swearValidationResult.getResultMessage() );
            }
            user.sendLangMessage( "chat-protection.swear", MessagePlaceholders.create().append( "swear-word", swearValidationResult.getSwearWord() ) );

            if ( config.exists( "commands" ) )
            {
                config.getStringList( "commands" ).forEach( command ->
                {
                    command = PlaceHolderAPI.formatMessage( user, command );
                    command = Utils.replacePlaceHolders( command, MessagePlaceholders.create().append( "swear-word", swearValidationResult.getSwearWord() ) );

                    BuX.getApi().getConsoleUser().executeCommand( command );
                } );
            }
        }
    }

    @Event( priority = Priority.HIGH, executeIfCancelled = false )
    public void onCapsChat( UserChatEvent event )
    {
        final User user = event.getUser();
        final String message = event.getMessage();
        final IConfiguration config = ConfigFiles.ANTICAPS.getConfig();

        if ( !ChatProtections.CAPS_PROTECTION.validateMessage( user, message ).isValid() )
        {
            if ( config.getBoolean( "cancel" ) )
            {
                event.setCancelled( true );
            }
            else
            {
                event.setMessage( event.getMessage().toLowerCase() );
            }
            user.sendLangMessage( "chat-protection.caps" );

            if ( config.exists( "commands" ) )
            {
                config.getStringList( "commands" ).forEach( command ->
                {
                    command = PlaceHolderAPI.formatMessage( user, command );

                    BuX.getApi().getConsoleUser().executeCommand( command );
                } );
            }
        }
    }

    @Event( priority = Priority.HIGH, executeIfCancelled = false )
    public void onSpamChat( UserChatEvent event )
    {
        final User user = event.getUser();
        final IConfiguration config = ConfigFiles.ANTISPAM.getConfig();

        if ( !ChatProtections.SPAM_PROTECTION.validateMessage( user, event.getMessage() ).isValid() )
        {
            event.setCancelled( true );
            user.sendLangMessage( "chat-protection.spam", MessagePlaceholders.create().append( "time", user.getCooldowns().getLeftTime( "CHATSPAM" ) / 1000 ) );

            if ( config.exists( "commands" ) )
            {
                config.getStringList( "commands" ).forEach( command ->
                {
                    command = PlaceHolderAPI.formatMessage( user, command );

                    BuX.getApi().getConsoleUser().executeCommand( command );
                } );
            }
        }
    }

    @Event( priority = Priority.HIGH, executeIfCancelled = false )
    public void onAdChat( UserChatEvent event )
    {
        final User user = event.getUser();
        final String message = event.getMessage();
        final IConfiguration config = ConfigFiles.ANTIAD.getConfig();

        if ( !ChatProtections.ADVERTISEMENT_PROTECTION.validateMessage( user, message ).isValid() )
        {
            event.setCancelled( true );

            user.sendLangMessage( "chat-protection.advertise" );

            if ( config.exists( "commands" ) )
            {
                config.getStringList( "commands" ).forEach( command ->
                {
                    command = PlaceHolderAPI.formatMessage( user, command );

                    BuX.getApi().getConsoleUser().executeCommand( command );
                } );
            }
        }
    }

    @Event
    public void executeChatConsumers( final UserChatEvent event )
    {
        if ( event.getUser().getStorage().hasData( UserStorageKey.CHAT_CONSUMERS ) )
        {
            final List<Consumer<String>> consumers = event.getUser().getStorage().getData( UserStorageKey.CHAT_CONSUMERS );

            if ( !consumers.isEmpty() )
            {
                event.setCancelled( true );
                for ( Consumer<String> consumer : consumers )
                {
                    consumer.accept( event.getMessage() );
                }
                consumers.clear();
            }
        }
    }

    @Event
    public void onPartyChat( final UserChatEvent event )
    {
        if ( BuX.getInstance().getPartyManager() == null )
        {
            return;
        }

        BuX.getInstance().getPartyManager().getCurrentPartyFor( event.getUser().getName() )
            .ifPresent( party -> party.getPartyMembers()
                .stream()
                .filter( m -> m.getUuid().equals( event.getUser().getUuid() ) )
                .filter( m -> m.isChat() )
                .findFirst()
                .ifPresent( member ->
                {
                    event.setCancelled( true );

                    BuX.getInstance().getPartyManager().languageBroadcastToParty(
                        party,
                        "party.chat.format",
                        MessagePlaceholders.create()
                            .append( "user", event.getUser().getName() )
                            .append( "message", event.getMessage() )
                    );
                } ) );
    }
}
