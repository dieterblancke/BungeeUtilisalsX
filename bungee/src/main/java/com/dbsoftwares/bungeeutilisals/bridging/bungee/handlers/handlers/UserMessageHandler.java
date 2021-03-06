package com.dbsoftwares.bungeeutilisals.bridging.bungee.handlers.handlers;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.text.LanguageUtils;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.handlers.BridgeMessageHandler;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.types.UserAction;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.util.BridgedUserMessage;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

public class UserMessageHandler implements BridgeMessageHandler<UserAction>
{

    @Override
    public void accept( final BridgeResponseEvent event, final UserAction action )
    {
        switch ( action.getType() )
        {
            case KICK:
            {
                final Optional<User> optionalUser = BUCore.getApi().getUser( action.getUser().getUuid() );
                if ( !optionalUser.isPresent() )
                {
                    return;
                }
                final User user = optionalUser.get();

                kickUser( user, action.getMessage() );
                break;
            }
            case KICK_IP:
            {
                BUCore.getApi().getUsers().stream()
                        .filter( user -> user.getIp().equalsIgnoreCase( action.getUser().getIp() ) )
                        .forEach( user -> kickUser( user, action.getMessage() ) );
                break;
            }
            case MESSAGE:
            {
                final BridgedUserMessage message = action.getMessage();
                final List<User> users = Lists.newArrayList();
                final List<String> permissions = message.getData().containsKey( "PERMISSION" )
                        ? (List<String>) message.getData().get( "PERMISSION" )
                        : null;

                if ( action.getUser() == null )
                {
                    // all users
                    users.addAll( BUCore.getApi().getUsers() );
                }
                else
                {
                    // specific user
                    final Optional<User> optionalUser = BUCore.getApi().getUser( action.getUser().getUuid() );
                    if ( !optionalUser.isPresent() )
                    {
                        return;
                    }
                    users.add( optionalUser.get() );
                }

                for ( User user : users )
                {
                    boolean canUse = false;
                    if ( permissions == null )
                    {
                        canUse = true;
                    }
                    else
                    {
                        for ( String permission : permissions )
                        {
                            if ( user.hasPermission( permission ) )
                            {
                                canUse = true;
                                break;
                            }
                        }
                    }

                    if ( canUse )
                    {
                        if ( message.isLanguage() )
                        {
                            user.sendLangMessage( message.getMessage(), message.getPlaceholders() );
                        }
                        else
                        {
                            user.sendRawColorMessage( message.getMessage() );
                        }
                    }
                }
                users.clear();
                break;
            }
            case MUTE:
            {
                final Optional<User> optionalUser = BUCore.getApi().getUser( action.getUser().getUuid() );
                if ( !optionalUser.isPresent() )
                {
                    return;
                }
                final User user = optionalUser.get();

                muteUser( user, action.getMessage() );
                break;
            }
            case MUTE_IP:
            {
                BUCore.getApi().getUsers().stream()
                        .filter( user -> user.getIp().equalsIgnoreCase( action.getUser().getIp() ) )
                        .forEach( user -> muteUser( user, action.getMessage() ) );
                break;
            }
        }
    }

    private void kickUser( final User user, final BridgedUserMessage message )
    {
        final String reason = (String) message.getData().get( "reason" );
        final String type = (String) message.getData().get( "type" );

        String kick = null;
        if ( BUCore.getApi().getPunishmentExecutor().isTemplateReason( reason ) )
        {
            kick = Utils.formatList( BUCore.getApi().getPunishmentExecutor().searchTemplate(
                    user.getLanguageConfig(), PunishmentType.valueOf( type ), reason
            ), "\n" );
        }
        if ( kick == null )
        {
            kick = Utils.formatList(
                    user.getLanguageConfig().getStringList( message.getMessage() ),
                    "\n"
            );
        }
        kick = LanguageUtils.replacePlaceHolders( user, kick, message.getPlaceholders() );
        user.kick( kick );
    }

    private void muteUser( final User user, final BridgedUserMessage message )
    {
        final String reason = (String) message.getData().get( "reason" );
        final String type = (String) message.getData().get( "type" );

        List<String> mute = null;
        if ( BUCore.getApi().getPunishmentExecutor().isTemplateReason( reason ) )
        {
            mute = BUCore.getApi().getPunishmentExecutor().searchTemplate(
                    user.getLanguageConfig(), PunishmentType.valueOf( type ), reason
            );
        }
        if ( mute == null )
        {
            user.sendLangMessage( "punishments.mute.onmute", message.getPlaceholders() );
        }
        else
        {
            mute.forEach( str -> user.sendRawColorMessage( LanguageUtils.replacePlaceHolders( user, str, message.getPlaceholders() ) ) );
        }
    }

    @Override
    public Class<? extends UserAction> getType()
    {
        return UserAction.class;
    }
}
