package dev.endoy.bungeeutilisalsx.common.commands.general.message;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.command.TabCall;
import dev.endoy.bungeeutilisalsx.common.api.command.TabCompleter;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.UserDao;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorage;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;

public class IgnoreCommandCall implements CommandCall, TabCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.isEmpty() )
        {
            user.sendLangMessage( "general-commands.ignore.usage" );
            return;
        }
        final String action = args.get( 0 );

        if ( action.equalsIgnoreCase( "add" ) || action.equalsIgnoreCase( "remove" ) )
        {
            if ( args.size() != 2 )
            {
                user.sendLangMessage( "general-commands.ignore.usage" );
                return;
            }
            final String name = args.get( 1 );

            if ( user.getName().equalsIgnoreCase( name ) )
            {
                user.sendLangMessage( "general-commands.ignore.self-ignore" );
                return;
            }

            final UserDao dao = BuX.getApi().getStorageManager().getDao().getUserDao();

            dao.getUserData( name ).thenAccept( optionalStorage ->
            {
                final UserStorage storage = optionalStorage.orElse( null );
                if ( storage == null || !storage.isLoaded() )
                {
                    user.sendLangMessage( "never-joined" );
                    return;
                }

                MessagePlaceholders placeholders = MessagePlaceholders.create()
                        .append( "user", name );

                if ( action.equalsIgnoreCase( "remove" ) )
                {
                    if ( user.getStorage().getIgnoredUsers().stream().noneMatch( ignored -> ignored.equalsIgnoreCase( name ) ) )
                    {
                        user.sendLangMessage(
                                "general-commands.ignore.remove.not-ignored",
                                placeholders
                        );
                        return;
                    }

                    BuX.getApi().getStorageManager().getDao().getUserDao().unignoreUser( user.getUuid(), storage.getUuid() );
                    user.getStorage().getIgnoredUsers().removeIf( ignored -> ignored.equalsIgnoreCase( name ) );

                    user.sendLangMessage(
                            "general-commands.ignore.remove.unignored",
                            placeholders
                    );
                }
                else
                {
                    if ( user.getStorage().getIgnoredUsers().stream().anyMatch( ignored -> ignored.equalsIgnoreCase( name ) ) )
                    {
                        user.sendLangMessage(
                                "general-commands.ignore.add.already-ignored",
                                placeholders
                        );
                        return;
                    }

                    BuX.getApi().getStorageManager().getDao().getUserDao().ignoreUser( user.getUuid(), storage.getUuid() );
                    user.getStorage().getIgnoredUsers().add( storage.getUserName() );

                    user.sendLangMessage(
                            "general-commands.ignore.add.ignored",
                            placeholders
                    );
                }
            } );
        }
        else if ( action.equalsIgnoreCase( "list" ) )
        {
            if ( user.getStorage().getIgnoredUsers().isEmpty() )
            {
                user.sendLangMessage( "general-commands.ignore.list.none" );
            }
            else
            {
                user.sendLangMessage(
                        "general-commands.ignore.list.message",
                        MessagePlaceholders.create()
                                .append( "{ignoredusers}", String.join(
                                        user.getLanguageConfig().getConfig().getString( "general-commands.ignore.list.separator" ),
                                        user.getStorage().getIgnoredUsers()
                                ) )
                );
            }
        }
        else
        {
            user.sendLangMessage( "general-commands.ignore.usage" );
        }
    }

    @Override
    public String getDescription()
    {
        return "Ignores private messages and friend requests from a user.";
    }

    @Override
    public String getUsage()
    {
        return "/ignore (add/remove/list) [user]";
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        return TabCompleter.empty();
    }
}
