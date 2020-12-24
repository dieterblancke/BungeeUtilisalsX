package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Priority;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.ChatLockCommandCall;
import com.dbsoftwares.configuration.api.IConfiguration;

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
            event.setMessage( BuX.getApi().getChatManager().fancyFont( message ) );
        }
    }

    @Event( priority = Priority.LOW, executeIfCancelled = false )
    public void onUnicodeSymbol( UserChatEvent event )
    {
        final IConfiguration config = ConfigFiles.UTFSYMBOLS.getConfig();

        if ( config.getBoolean( "symbols.enabled" ) && event.getUser().hasPermission( config.getString( "symbols.permission" ) ) )
        {
            event.setMessage( event.getApi().getChatManager().replaceSymbols( event.getMessage() ) );
        }
    }

    @Event( priority = Priority.HIGH, executeIfCancelled = false )
    public void onSwearChat( UserChatEvent event )
    {
        final User user = event.getUser();
        final String message = event.getMessage();
        final IConfiguration config = ConfigFiles.ANTISWEAR.getConfig();

        if ( BuX.getApi().getChatManager().checkForSwear( event.getUser(), message ) )
        {
            if ( config.getBoolean( "cancel" ) )
            {
                event.setCancelled( true );
            }
            else
            {
                event.setMessage( BuX.getApi().getChatManager().replaceSwearWords( user, message, config.getString( "replace" ) ) );
            }
            user.sendLangMessage( "chat-protection.swear" );

            if ( config.exists( "commands" ) )
            {
                config.getStringList( "commands" ).forEach( command ->
                {
                    command = PlaceHolderAPI.formatMessage( user, command );

                    user.executeCommand( command );
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

        if ( BuX.getApi().getChatManager().checkForCaps( user, message ) )
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

                    user.executeCommand( command );
                } );
            }
        }
    }

    @Event( priority = Priority.HIGH, executeIfCancelled = false )
    public void onSpamChat( UserChatEvent event )
    {
        final User user = event.getUser();
        final IConfiguration config = ConfigFiles.ANTISPAM.getConfig();

        if ( BuX.getApi().getChatManager().checkForSpam( user ) )
        {
            event.setCancelled( true );

            user.sendLangMessage( "chat-protection.spam", "%time%", user.getCooldowns().getLeftTime( "CHATSPAM" ) / 1000 );

            if ( config.exists( "commands" ) )
            {
                config.getStringList( "commands" ).forEach( command ->
                {
                    command = PlaceHolderAPI.formatMessage( user, command );

                    user.executeCommand( command );
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

        if ( BuX.getApi().getChatManager().checkForAdvertisement( user, message ) )
        {
            event.setCancelled( true );

            user.sendLangMessage( "chat-protection.advertise" );

            if ( config.exists( "commands" ) )
            {
                config.getStringList( "commands" ).forEach( command ->
                {
                    command = PlaceHolderAPI.formatMessage( user, command );

                    user.executeCommand( command );
                } );
            }
        }
    }
}
