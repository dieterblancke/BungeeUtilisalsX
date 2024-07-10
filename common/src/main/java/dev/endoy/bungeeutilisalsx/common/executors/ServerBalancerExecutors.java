package dev.endoy.bungeeutilisalsx.common.executors;

import dev.endoy.bungeeutilisalsx.common.api.event.event.Event;
import dev.endoy.bungeeutilisalsx.common.api.event.event.EventExecutor;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserServerConnectEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserServerConnectEvent.ConnectReason;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserServerKickEvent;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.serverbalancer.ServerBalancer;
import dev.endoy.bungeeutilisalsx.common.api.user.CooldownConstants;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class ServerBalancerExecutors implements EventExecutor
{

    private final ServerBalancer serverBalancer;

    @Event
    public void onConnectToBalancedGroup( UserServerConnectEvent event )
    {
        if ( !this.shouldBalance( event.getConnectReason() ) || !event.getUser().getCooldowns().canUse( CooldownConstants.SERVER_SWITCH_SERVER_BALANCER_COOLDOWN ) )
        {
            return;
        }
        IProxyServer target = event.getTarget();

        ConfigFiles.SERVER_BALANCER_CONFIG.getServerBalancerGroupFor( target.getName() )
                .flatMap( serverBalancer::getOptimalServer )
                .ifPresent( event::setTarget );
    }

    @Event
    public void onKick( UserServerKickEvent event )
    {
        if ( event.getKickMessage() == null )
        {
            return;
        }
        if ( !event.getUser().getCooldowns().canUse( CooldownConstants.SERVER_SWITCH_SERVER_BALANCER_COOLDOWN ) )
        {
            return;
        }

        if ( this.shouldRedirect( event.getKickMessage() ) )
        {
            ofNullable( ConfigFiles.SERVER_BALANCER_CONFIG.getFallbackConfig().getFallbackGroup() )
                    .flatMap( serverBalancer::getOptimalServer )
                    .ifPresent( event::setRedirectServer );
        }
    }

    private boolean shouldRedirect( Component kickMessage )
    {
        String textContents = LegacyComponentSerializer.legacyAmpersand().serialize( kickMessage ).toLowerCase();

        return switch ( ConfigFiles.SERVER_BALANCER_CONFIG.getFallbackConfig().getFallbackMode() )
                {
                    case BLACKLIST ->
                    {
                        for ( String reason : ConfigFiles.SERVER_BALANCER_CONFIG.getFallbackConfig().getReasons() )
                        {
                            if ( textContents.contains( reason.toLowerCase() ) )
                            {
                                yield false;
                            }
                        }

                        yield true;
                    }
                    case WHITELIST ->
                    {
                        for ( String reason : ConfigFiles.SERVER_BALANCER_CONFIG.getFallbackConfig().getReasons() )
                        {
                            if ( textContents.contains( reason.toLowerCase() ) )
                            {
                                yield true;
                            }
                        }

                        yield false;
                    }
                };
    }

    private boolean shouldBalance( ConnectReason connectReason )
    {
        return switch ( connectReason )
                {
                    case LOBBY_FALLBACK, UNKNOWN, JOIN_PROXY, KICK_REDIRECT, SERVER_DOWN_REDIRECT -> true;
                    case COMMAND, PLUGIN, PLUGIN_MESSAGE -> false;
                };
    }
}