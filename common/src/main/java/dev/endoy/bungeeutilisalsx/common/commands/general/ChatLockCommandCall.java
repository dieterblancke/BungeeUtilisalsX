package dev.endoy.bungeeutilisalsx.common.commands.general;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.command.TabCall;
import dev.endoy.bungeeutilisalsx.common.api.command.TabCompleter;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.ChatLockJob;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Stream;

public class ChatLockCommandCall implements CommandCall, TabCall
{

    public static final List<String> lockedChatServers = Lists.newArrayList();

    public static void lockChat( final String server, final String by )
    {
        MessagePlaceholders placeholders = MessagePlaceholders.create()
            .append( "user", by );

        Stream<User> users = server.equals( "ALL" )
            ? BuX.getApi().getUsers().stream()
            : BuX.getApi().getUsers().stream().filter( u -> u.getServerName().equalsIgnoreCase( server ) );

        if ( lockedChatServers.contains( server ) )
        {
            lockedChatServers.remove( server );

            users.forEach( u -> u.sendLangMessage( "general-commands.chatlock.unlocked", placeholders ) );
        }
        else
        {
            lockedChatServers.add( server );

            users.forEach( u -> u.sendLangMessage( "general-commands.chatlock.locked", placeholders ) );
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
