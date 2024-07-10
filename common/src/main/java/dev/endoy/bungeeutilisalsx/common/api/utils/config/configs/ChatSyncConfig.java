package dev.endoy.bungeeutilisalsx.common.api.utils.config.configs;

import dev.endoy.bungeeutilisalsx.common.api.utils.config.Config;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class ChatSyncConfig extends Config
{

    private final List<ChatSyncedServer> syncedServers = new ArrayList<>();

    public ChatSyncConfig( final String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        this.syncedServers.clear();
    }

    @Override
    protected void setup()
    {
        if ( config == null || !isEnabled() )
        {
            return;
        }

        syncedServers.clear();
        syncedServers.addAll(
                config.getSectionList( "synced-servers" )
                        .stream()
                        .map( section -> new ChatSyncedServer(
                                ConfigFiles.SERVERGROUPS.getServer( section.getString( "server" ) ).orElse( null ),
                                section.getString( "format" ),
                                section.getBoolean( "force-format" )
                        ) )
                        .filter( it -> it.serverGroup() != null )
                        .toList()
        );
    }

    public Optional<ChatSyncedServer> getChatSyncedServer( final String serverName )
    {
        return syncedServers.stream()
                .filter( it -> it.serverGroup().isInGroup( serverName ) )
                .findFirst();
    }

    public record ChatSyncedServer(ServerGroup serverGroup, String format, boolean forceFormat)
    {
    }
}