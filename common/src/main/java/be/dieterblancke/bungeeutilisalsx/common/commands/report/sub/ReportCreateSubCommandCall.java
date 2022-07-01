package be.dieterblancke.bungeeutilisalsx.common.commands.report.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ReportsDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.DiscordWebhook.EmbedObject;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.Report;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import be.dieterblancke.bungeeutilisalsx.common.webhook.WebhookFactory;
import be.dieterblancke.configuration.api.ISection;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReportCreateSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 2 )
        {
            user.sendLangMessage( "general-commands.report.create.usage" );
            return;
        }

        final String targetName = args.get( 0 );
        final String reason = String.join( " ", args.subList( 1, args.size() ) );

        if ( !BuX.getApi().getPlayerUtils().isOnline( targetName ) )
        {
            user.sendLangMessage( "offline" );
            return;
        }
        final String bypassPermission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "report.bypass" );
        final Optional<User> optionalUser = BuX.getApi().getUser( targetName );
        if ( optionalUser.isPresent() )
        {
            final User target = optionalUser.get();

            if ( target.hasPermission( bypassPermission ) )
            {
                user.sendLangMessage( "general-commands.report.create.bypassed" );
                return;
            }
        }

        final UUID targetUuid = BuX.getApi().getPlayerUtils().getUuid( targetName );
        final Report report = new Report(
                -1,
                targetUuid,
                targetName,
                user.getName(),
                new Date(),
                user.getServerName(),
                reason,
                false,
                false
        );
        final ReportsDao reportsDao = BuX.getApi().getStorageManager().getDao().getReportsDao();

        reportsDao.addReport( report );
        user.sendLangMessage( "general-commands.report.create.created", MessagePlaceholders.create().append( "target", targetName ) );

        BuX.getApi().langPermissionBroadcast(
                "general-commands.report.create.broadcast",
                ConfigFiles.GENERALCOMMANDS.getConfig().getString( "report.subcommands.create.broadcast" ),
                MessagePlaceholders.create()
                        .append( "target", targetName )
                        .append( "user", user.getName() )
                        .append( "reason", reason )
                        .append( "server", user.getServerName() )
        );

        this.sendDiscordWebhook( report );
    }

    @Override
    public String getDescription()
    {
        return "Creates a new report against a user with a given description.";
    }

    @Override
    public String getUsage()
    {
        return "/report create (user) (reason)";
    }

    private void sendDiscordWebhook( final Report report )
    {
        final ISection discordSection = ConfigFiles.GENERALCOMMANDS.getConfig().getSection( "report.discord-webhook" );

        if ( !ConfigFiles.WEBHOOK_CONFIG.getDiscordWebhook().enabled() || !discordSection.getBoolean( "enabled" ) )
        {
            return;
        }

        WebhookFactory.discord().send( EmbedObject.fromSection(
                discordSection,
                report
        ) );
    }
}
