package dev.endoy.bungeeutilisalsx.common.api.utils.config;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.configs.*;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;

public class ConfigFiles
{

    public static MainConfig CONFIG = new MainConfig( "/configurations/config.yml" );
    public static ServerGroupsConfig SERVERGROUPS = new ServerGroupsConfig( "/configurations/servergroups.yml" );
    public static MotdConfig MOTD = new MotdConfig( "/configurations/motd/motd.yml" );
    public static Config CUSTOMCOMMANDS = new Config( "/configurations/commands/customcommands.yml" );
    public static Config GENERALCOMMANDS = new Config( "/configurations/commands/generalcommands.yml" );
    public static CommandBlockerConfig COMMAND_BLOCKER = new CommandBlockerConfig( "/configurations/commands/commandblocker.yml" );
    public static Config ANTISWEAR = new Config( "/configurations/chat/protection/antiswear.yml" );
    public static Config ANTICAPS = new Config( "/configurations/chat/protection/anticaps.yml" );
    public static Config ANTIAD = new Config( "/configurations/chat/protection/antiadvertise.yml" );
    public static Config ANTISPAM = new Config( "/configurations/chat/protection/antispam.yml" );
    public static Config UTFSYMBOLS = new Config( "/configurations/chat/utfsymbols.yml" );
    public static FriendsConfig FRIENDS_CONFIG = new FriendsConfig( "/configurations/friends.yml" );
    public static Config PUNISHMENT_CONFIG = new Config( "/configurations/punishments/config.yml" );
    public static PunishmentsActionsConfig PUNISHMENT_ACTIONS = new PunishmentsActionsConfig( "/configurations/punishments/actions.yml" );
    public static PunishmentsTracksConfig PUNISHMENT_TRACKS = new PunishmentsTracksConfig( "/configurations/punishments/tracks.yml" );
    public static Config LANGUAGES_CONFIG = new Config( "/configurations/languages/config.yml" );
    public static RanksConfig RANKS = new RanksConfig( "/configurations/chat/ranks.yml" );
    public static IngameMotdConfig INGAME_MOTD_CONFIG = new IngameMotdConfig( "/configurations/motd/ingame-motd.yml" );
    public static WebhookConfig WEBHOOK_CONFIG = new WebhookConfig( "/configurations/webhooks.yml" );
    public static PartyConfig PARTY_CONFIG = new PartyConfig( "/configurations/party/config.yml" );
    public static ChatSyncConfig CHAT_SYNC_CONFIG = new ChatSyncConfig( "/configurations/chat/chatsync.yml" );
    public static ServerBalancerConfig SERVER_BALANCER_CONFIG = new ServerBalancerConfig( "/configurations/serverbalancer.yml" );

    public static void loadAllConfigs()
    {
        final List<Config> configs = getAllConfigs();

        for ( Config config : configs )
        {
            config.load();
        }
        BuX.getLogger().info( "Finished loaded config files" );
    }

    public static void reloadAllConfigs()
    {
        final List<Config> configs = getAllConfigs();

        for ( Config config : configs )
        {
            config.reload();
        }
    }

    public static List<Config> getAllConfigs()
    {
        final List<Config> configs = Lists.newArrayList();

        for ( Field field : ConfigFiles.class.getFields() )
        {
            try
            {
                final Object value = field.get( null );

                if ( value instanceof Config )
                {
                    configs.add( (Config) value );
                }
            }
            catch ( IllegalAccessException e )
            {
                // ignore
            }
        }
        return configs;
    }
}
