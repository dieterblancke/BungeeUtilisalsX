package dev.endoy.bungeeutilisalsx.common.commands.punishments;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.Dao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.UserDao;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorage;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class CheckIpCommandCall implements CommandCall
{

    private static final Pattern IP_PATTERN = Pattern.compile( "^(?:(?:^|\\.)(?:2(?:5[0-5]|[0-4]\\d)|1?\\d?\\d)){4}$" );

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "punishments.checkip.usage" );
            return;
        }

        // Running it async ...
        BuX.getInstance().getScheduler().runAsync( () ->
        {
            final Dao dao = BuX.getApi().getStorageManager().getDao();
            final UserDao userDao = dao.getUserDao();
            final PunishmentDao punishmentDao = dao.getPunishmentDao();

            final boolean exists = IP_PATTERN.matcher( args.get( 0 ) ).find()
                ? userDao.ipExists( args.get( 0 ) ).join()
                : userDao.exists( args.get( 0 ) ).join();

            if ( !exists )
            {
                user.sendLangMessage( "never-joined" );
                return;
            }
            final Optional<UserStorage> optionalStorage = userDao.getUserData( args.get( 0 ) ).join();
            final UserStorage storage = optionalStorage.orElse( null );
            if ( storage == null || !storage.isLoaded() )
            {
                user.sendLangMessage( "never-joined" );
                return;
            }

            user.sendLangMessage(
                "punishments.checkip.head",
                MessagePlaceholders.create()
                    .append( "user", storage.getUserName() )
                    .append( "ip", storage.getIp() )
            );

            final List<String> users = userDao.getUsersOnIP( storage.getIp() ).join();
            final List<String> formattedUsers = Lists.newArrayList();

            users.forEach( u ->
            {
                final boolean banned = punishmentDao.getBansDao().isBanned( storage.getUuid(), "ALL" ).join();
                final boolean ipbanned = punishmentDao.getBansDao().isIPBanned( storage.getIp(), "ALL" ).join();

                String colorPath = "punishments.checkip.colors.";
                if ( banned || ipbanned )
                {
                    colorPath += "banned";
                }
                else
                {
                    if ( BuX.getApi().getPlayerUtils().isOnline( u ) )
                    {
                        colorPath += "online";
                    }
                    else
                    {
                        colorPath += "offline";
                    }
                }

                formattedUsers.add( user.getLanguageConfig().getConfig().getString( colorPath ) + u );
            } );

            user.sendLangMessage(
                "punishments.checkip.format.message",
                MessagePlaceholders.create()
                    .append( "players", Utils.formatList(
                        formattedUsers, user.getLanguageConfig().getConfig().getString( "punishments.checkip.format.separator" )
                    ) )
            );

            user.sendLangMessage(
                "punishments.checkip.foot",
                MessagePlaceholders.create()
                    .append( "user", storage.getUserName() )
                    .append( "ip", storage.getIp() )
            );
        } );
    }

    @Override
    public String getDescription()
    {
        return "Checks the accounts on a given IP and their current ban status.";
    }

    @Override
    public String getUsage()
    {
        return "/checkip (user / ip)";
    }
}