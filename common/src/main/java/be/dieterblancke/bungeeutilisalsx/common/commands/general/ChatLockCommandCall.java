package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCompleter;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.ChatLockJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Stream;

public class ChatLockCommandCall implements CommandCall, TabCall
{

    public static final List<String> lockedChatServers = Lists.newArrayList();

    public static void lockChat( final String server, final String by )
    {
        final Stream<User> users = server.equals( "ALL" )
                ? BuX.getApi().getUsers().stream()
                : BuX.getApi().getUsers().stream().filter( u -> u.getServerName().equalsIgnoreCase( server ) );

        if ( lockedChatServers.contains( server ) )
        {
            lockedChatServers.remove( server );

            users.forEach( u -> u.sendLangMessage( "general-commands.chatlock.unlocked", "{user}", by ) );
        }
        else
        {
            lockedChatServers.add( server );

            users.forEach( u -> u.sendLangMessage( "general-commands.chatlock.locked", "{user}", by ) );
        }
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
            user.sendLangMessage( "general-commands.chatlock.usage" );
            return;
        }
        final String server = args.get( 0 ).toLowerCase().contains( "g" ) ? "ALL" : user.getServerName();

        BuX.getInstance().getJobManager().executeJob( new ChatLockJob( server, user.getName() ) );
    }

    @Override
    public String getDescription()
    {
        return "Locks the chat globally or in a specfic server.";
    }

    @Override
    public String getUsage()
    {
        return "/chatlock (server / ALL)";
    }
}
