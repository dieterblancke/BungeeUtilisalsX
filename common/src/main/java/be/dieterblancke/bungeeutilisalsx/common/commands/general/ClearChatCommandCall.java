package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCompleter;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.ClearChatJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;

public class ClearChatCommandCall implements CommandCall, TabCall
{

    public static void clearChat( final String server, final String by )
    {
        if ( server.equalsIgnoreCase( "ALL" ) )
        {
            BuX.getApi().getUsers().forEach( u -> clearChat( u, by ) );
        }
        else
        {
            final IProxyServer info = BuX.getInstance().proxyOperations().getServerInfo( server );

            if ( info != null )
            {
                BuX.getApi().getUsers()
                        .stream()
                        .filter( u -> u.getServerName().equalsIgnoreCase( info.getName() ) )
                        .forEach( u -> clearChat( u, by ) );
            }
        }
    }

    private static void clearChat( final User user, final String by )
    {
        for ( int i = 0; i < 250; i++ )
        {
            user.sendMessage( Utils.format( "&e  " ) );
        }

        user.sendLangMessage(
                "general-commands.clearchat.cleared",
                MessagePlaceholders.create().append( "user", by )
        );
    }

    @Override
    public List<String> onTabComplete( final User user, final String[] args )
    {
        return TabCompleter.buildTabCompletion( ConfigFiles.SERVERGROUPS.getServers().keySet(), args );
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "general-commands.clearchat.usage" );
            return;
        }
        final String server = args.get( 0 ).toLowerCase().contains( "g" ) ? "ALL" : user.getServerName();

        BuX.getInstance().getJobManager().executeJob( new ClearChatJob( server, user.getName() ) );
    }

    @Override
    public String getDescription()
    {
        return "Clears the chat globally or in a specfic server.";
    }

    @Override
    public String getUsage()
    {
        return "/clearchat (server / ALL)";
    }
}
