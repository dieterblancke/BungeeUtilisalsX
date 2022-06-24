package be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs;

import be.dieterblancke.bungeeutilisalsx.common.announcers.bossbar.BossBarMessage;
import be.dieterblancke.bungeeutilisalsx.common.announcers.title.TitleMessage;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class IngameMotdConfig extends Config
{

    @Getter
    private final List<IngameMotd> motds = new ArrayList<>();

    public IngameMotdConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        motds.clear();
    }

    @Override
    public void setup()
    {
        motds.addAll(
                config.getSectionList( "motds" )
                        .stream()
                        .map( section -> new IngameMotd(
                                section.exists( "server" )
                                        ? ConfigFiles.SERVERGROUPS.getServer( section.getString( "server" ) ).orElse( null )
                                        : null,
                                section.exists( "once-per-session" ) && section.getBoolean( "once-per-session" ),
                                section.exists( "language" ) && section.getBoolean( "language" ),
                                section.isString( "message" )
                                        ? Collections.singletonList( section.getString( "message" ) )
                                        : section.getStringList( "message" ),
                                section.exists( "actionbar" )
                                        ? section.getString( "actionbar" )
                                        : null,
                                section.exists( "bossbar" )
                                        ? new BossBarMessage( section.getSection( "bossbar" ) )
                                        : null,
                                section.exists( "title" )
                                        ? new TitleMessage( section.getSection( "title" ) )
                                        : null,
                                section.exists( "receive-permission" )
                                        ? section.getString( "receive-permission" )
                                        : null
                        ) )
                        .collect( Collectors.toList() )
        );
    }

    public List<IngameMotd> getApplicableMotds( final IProxyServer server )
    {
        return motds
                .stream()
                .filter( motd -> motd.getServer() == null || ( server != null && motd.getServer().isInGroup( server.getName() ) ) )
                .collect( Collectors.toList() );
    }

    @Value
    public static class IngameMotd
    {
        UUID uuid = UUID.randomUUID();
        ServerGroup server;
        boolean oncePerSession;
        boolean language;
        List<String> message;
        String actionBar;
        BossBarMessage bossBar;
        TitleMessage title;
        String receivePermission;

        public boolean hasActionBar()
        {
            return actionBar != null;
        }

        public boolean hasBossBar()
        {
            return bossBar != null;
        }

        public boolean hasTitle()
        {
            return title != null;
        }

        public boolean hasReceivePermission()
        {
            return receivePermission != null;
        }
    }
}
