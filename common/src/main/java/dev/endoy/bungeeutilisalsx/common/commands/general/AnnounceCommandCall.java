package dev.endoy.bungeeutilisalsx.common.commands.general;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import dev.endoy.bungeeutilisalsx.common.api.bossbar.BarColor;
import dev.endoy.bungeeutilisalsx.common.api.bossbar.BarStyle;
import dev.endoy.bungeeutilisalsx.common.api.bossbar.IBossBar;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.command.TabCall;
import dev.endoy.bungeeutilisalsx.common.api.command.TabCompleter;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.AnnounceJob;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.TimeUnit;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.api.utils.text.MessageBuilder;
import dev.endoy.configuration.api.IConfiguration;
import dev.endoy.configuration.api.ISection;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class AnnounceCommandCall implements CommandCall, TabCall
{

    public static void sendAnnounce( final Set<AnnouncementType> types, final String message )
    {
        for ( AnnouncementType type : types )
        {
            sendAnnounce( type, message );
        }
    }

    private static void sendAnnounce( AnnouncementType type, String message )
    {
        final IConfiguration config = ConfigFiles.GENERALCOMMANDS.getConfig();

        switch ( type )
        {
            case PRECONFIGURED:
                final String preconfiguredPath = config.getString( "announce.pre_configured" );

                for ( User user : BuX.getApi().getUsers() )
                {
                    if ( !user.getLanguageConfig().getConfig().exists( preconfiguredPath + "." + message ) )
                    {
                        continue;
                    }
                    final ISection section = user.getLanguageConfig().getConfig().getSection( preconfiguredPath + "." + message );

                    if ( section.exists( "chat" ) )
                    {
                        if ( section.isSection( "chat" ) )
                        {
                            user.sendMessage(
                                    MessageBuilder.buildMessage(
                                            user, section.getSection( "chat" ),
                                            MessagePlaceholders.create()
                                                    .append( "prefix", config.getString( "announce.types.chat.prefix" ) )
                                    )
                            );
                        }
                        else
                        {
                            for ( String line : section.getString( "chat" ).split( "%nl%" ) )
                            {
                                user.sendRawColorMessage(
                                        config.getString( "announce.types.chat.prefix" ) + line.replace( "%sub%", "" )
                                );
                            }
                        }
                    }
                    if ( section.exists( "actionbar" ) )
                    {
                        sendActionBar( section.getString( "actionbar" ) );
                    }
                    if ( section.exists( "title" ) )
                    {
                        sendTitle( section.getSection( "title" ) );
                    }
                    if ( section.exists( "bossbar" ) )
                    {
                        sendBossBar( section.getString( "bossbar" ) );
                    }
                }
                break;
            case CHAT:
                if ( config.getBoolean( "announce.types.chat.enabled" ) )
                {
                    final String prefix = config.getString( "announce.types.chat.prefix" );

                    for ( String line : message.split( "%nl%" ) )
                    {
                        line = line.replace( "%sub%", "" );

                        for ( User user : BuX.getApi().getUsers() )
                        {
                            user.sendMessage( prefix, line );
                        }
                        BuX.getApi().getConsoleUser().sendMessage( prefix, line );
                    }
                }
                break;
            case ACTIONBAR:
                sendActionBar( message );
                break;
            case TITLE:
                final String[] splitten = message.replace( "%nl%", "" ).split( "%sub%" );

                final String title = splitten[0];
                final String subtitle = splitten.length > 1 ? splitten[1] : "";
                sendTitle( title, subtitle );
                break;
            case BOSSBAR:
                sendBossBar( message );
                break;
        }
    }

    private static void sendBossBar( final String message )
    {
        final IConfiguration config = ConfigFiles.GENERALCOMMANDS.getConfig();
        final List<IBossBar> bossBars = Lists.newArrayList();

        final BarColor color = BarColor.valueOf( config.getString( "announce.types.bossbar.color" ) );
        final BarStyle style = BarStyle.valueOf( config.getString( "announce.types.bossbar.style" ) );
        float progress = config.getFloat( "announce.types.bossbar.progress" );
        long stay = config.getInteger( "announce.types.bossbar.stay" );

        BuX.getApi().getUsers().forEach( user ->
        {
            IBossBar bossBar = BuX.getApi().createBossBar(
                    color, style, progress,
                    Utils.format( user, message.replace( "%sub%", "" ).replace( "%nl%", "" ) )
            );

            bossBar.addUser( user );
            bossBars.add( bossBar );
        } );

        BuX.getInstance().getScheduler().runTaskDelayed(
                stay,
                TimeUnit.SECONDS,
                () -> bossBars.forEach( bossBar ->
                {
                    bossBar.clearUsers();

                    bossBar.unregister();
                } )
        );
    }

    private static void sendActionBar( final String message )
    {
        if ( ConfigFiles.GENERALCOMMANDS.getConfig().getBoolean( "announce.types.actionbar.enabled" ) )
        {
            for ( User user : BuX.getApi().getUsers() )
            {
                user.sendActionBar( Utils.formatString(
                        user,
                        message.replace( "%nl%", "" ).replace( "%sub%", "" )
                ) );
            }
        }
    }

    private static void sendTitle( final ISection section )
    {
        sendTitle( section.getString( "main" ), section.getString( "sub" ) );
    }

    private static void sendTitle( final String title, final String subtitle )
    {
        final IConfiguration config = ConfigFiles.GENERALCOMMANDS.getConfig();

        if ( config.getBoolean( "announce.types.title.enabled" ) )
        {
            final int fadein = config.getInteger( "announce.types.title.fadein" );
            final int stay = config.getInteger( "announce.types.title.stay" );
            final int fadeout = config.getInteger( "announce.types.title.fadeout" );

            for ( User user : BuX.getApi().getUsers() )
            {
                user.sendTitle(
                        Utils.formatString( user, title ),
                        Utils.formatString( user, subtitle ),
                        fadein,
                        stay,
                        fadeout
                );
            }
        }
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        return TabCompleter.empty();
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final String defaultsTo = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "announce.default-to", "none" );
        final int minArgs = defaultsTo.equalsIgnoreCase( "none" ) ? 2 : 1;

        if ( args.size() >= minArgs )
        {
            final String types;
            final String message;

            if ( defaultsTo.equalsIgnoreCase( "none" ) )
            {
                types = args.get( 0 );
                message = String.join( " ", args.subList( 1, args.size() ) );
            }
            else
            {
                if ( args.get( 0 ).startsWith( "type:" ) )
                {
                    types = args.get( 0 ).replace( "type:", "" );
                    message = String.join( " ", args.subList( 1, args.size() ) );
                }
                else
                {
                    types = defaultsTo;
                    message = String.join( " ", args );
                }
            }

            BuX.getInstance().getJobManager().executeJob( new AnnounceJob( this.getTypes( types ), message ) );
        }
        else
        {
            user.sendLangMessage( "general-commands.announce.usage" );
        }
    }

    @Override
    public String getDescription()
    {
        return """
                Announces a message globally (similarly to alert). This can be done over chat, title, actionbar and bossbar.
                Title and subtitles can be split using %sub%.
                If a default type is set up in the config, a type can still be overridden by using type:(types) as first parameter.
                Types can be concatinated, for example 'bcat' will announce bossbar, chat, actionbar and title at the same time.""";
    }

    @Override
    public String getUsage()
    {
        return "/announce (p/b/c/a/t) (message)";
    }

    private Set<AnnouncementType> getTypes( String types )
    {
        final Set<AnnouncementType> announcementTypes = Sets.newHashSet();

        if ( types.contains( "p" ) )
        {
            announcementTypes.add( AnnouncementType.PRECONFIGURED );
            return announcementTypes;
        }
        if ( types.contains( "a" ) )
        {
            announcementTypes.add( AnnouncementType.ACTIONBAR );
        }
        if ( types.contains( "c" ) )
        {
            announcementTypes.add( AnnouncementType.CHAT );
        }
        if ( types.contains( "t" ) )
        {
            announcementTypes.add( AnnouncementType.TITLE );
        }
        if ( types.contains( "b" ) )
        {
            announcementTypes.add( AnnouncementType.BOSSBAR );
        }

        return announcementTypes;
    }
}
