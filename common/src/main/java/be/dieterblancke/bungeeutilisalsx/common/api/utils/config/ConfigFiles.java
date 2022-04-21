package be.dieterblancke.bungeeutilisalsx.common.api.utils.config;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.*;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;

public class ConfigFiles
{

    public static MainConfig CONFIG = new MainConfig( "/config.yml" );
    public static ServerGroupsConfig SERVERGROUPS = new ServerGroupsConfig( "/servergroups.yml" );
    public static MotdConfig MOTD = new MotdConfig( "/motd.yml" );
    public static Config CUSTOMCOMMANDS = new Config( "/commands/customcommands.yml" );
    public static Config GENERALCOMMANDS = new Config( "/commands/generalcommands.yml" );
    public static CommandBlockerConfig COMMAND_BLOCKER = new CommandBlockerConfig( "/commands/commandblocker.yml" );
    public static Config ANTISWEAR = new Config( "/chat/protection/antiswear.yml" );
    public static Config ANTICAPS = new Config( "/chat/protection/anticaps.yml" );
    public static Config ANTIAD = new Config( "/chat/protection/antiadvertise.yml" );
    public static Config ANTISPAM = new Config( "/chat/protection/antispam.yml" );
    public static Config UTFSYMBOLS = new Config( "/chat/utfsymbols.yml" );
    public static FriendsConfig FRIENDS_CONFIG = new FriendsConfig( "/friends.yml" );
    public static Config PUNISHMENT_CONFIG = new Config( "/punishments/config.yml" );
    public static PunishmentsActionsConfig PUNISHMENT_ACTIONS = new PunishmentsActionsConfig( "/punishments/actions.yml" );
    public static PunishmentsTracksConfig PUNISHMENT_TRACKS = new PunishmentsTracksConfig( "/punishments/tracks.yml" );
    public static Config LANGUAGES_CONFIG = new Config( "/languages/config.yml" );
    public static RanksConfig RANKS = new RanksConfig( "/chat/ranks.yml" );
    public static Config HUBBALANCER = new Config( "/hubbalancer.yml" );
    public static IngameMotdConfig INGAME_MOTD_CONFIG = new IngameMotdConfig( "/ingame-motd.yml" );
    public static WebhookConfig WEBHOOK_CONFIG = new WebhookConfig( "/webhooks.yml" );
    public static PartyConfig PARTY_CONFIG = new PartyConfig( "/party/config.yml" );
    public static ChatSyncConfig CHAT_SYNC_CONFIG = new ChatSyncConfig( "/chat/chatsync.yml" );

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
