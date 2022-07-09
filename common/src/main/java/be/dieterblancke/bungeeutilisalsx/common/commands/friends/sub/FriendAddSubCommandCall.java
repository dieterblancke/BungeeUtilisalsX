package be.dieterblancke.bungeeutilisalsx.common.commands.friends.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import net.luckperms.api.messenger.message.Message;

import java.util.List;
import java.util.Optional;

public class FriendAddSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "friends.add.usage" );
            return;
        }
        final int friendLimit = FriendUtils.getFriendLimit( user );

        if ( user.getFriends().size() >= friendLimit )
        {
            user.sendLangMessage( "friends.add.limited", MessagePlaceholders.create().append( "limit", friendLimit ) );
            return;
        }
        final String name = args.get( 0 );
        final Dao dao = BuX.getApi().getStorageManager().getDao();

        if ( user.getName().equalsIgnoreCase( name ) )
        {
            user.sendLangMessage( "friends.add.selfadd" );
            return;
        }

        if ( user.getFriends().stream().anyMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            user.sendLangMessage( "friends.add.already-friend", MessagePlaceholders.create().append( "friend", name ) );
            return;
        }

        final Optional<User> optionalTarget = BuX.getApi().getUser( name );
        final UserStorage storage = Utils.getUserStorageIfUserExists( optionalTarget.orElse( null ), name );

        if ( storage == null )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }

        final boolean accepts = optionalTarget
                .map( value -> value.getFriendSettings().getSetting( FriendSetting.REQUESTS ) )
                .orElseGet( () -> dao.getFriendsDao().getSetting( storage.getUuid(), FriendSetting.REQUESTS ).join() );
        final boolean isIgnored = storage.getIgnoredUsers().stream().anyMatch( u -> u.equalsIgnoreCase( user.getName() ) );

        if ( !accepts || isIgnored )
        {
            user.sendLangMessage( "friends.add.disallowed" );
            return;
        }

        if ( dao.getFriendsDao().hasOutgoingFriendRequest( user.getUuid(), storage.getUuid() ).join() )
        {
            user.sendLangMessage( "friends.add.already-requested", MessagePlaceholders.create().append( "name", name ) );
            return;
        }
        if ( dao.getFriendsDao().hasIncomingFriendRequest( user.getUuid(), storage.getUuid() ).join() )
        {
            FriendAcceptSubCommandCall.acceptFriendRequest( user, storage, optionalTarget.orElse( null ) );
            return;
        }

        dao.getFriendsDao().addFriendRequest( user.getUuid(), storage.getUuid() );
        user.sendLangMessage( "friends.add.request-sent", MessagePlaceholders.create().append( "user", name ) );

        if ( optionalTarget.isPresent() )
        {
            final User target = optionalTarget.get();

            target.sendLangMessage( "friends.request-received", MessagePlaceholders.create().append( "name", user.getName() ) );
        }
        else if ( BuX.getApi().getPlayerUtils().isOnline( name ) )
        {
            BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                    name,
                    "friends.request-received",
                    MessagePlaceholders.create().append( "name", user.getName() )
            ) );
        }
    }

    @Override
    public String getDescription()
    {
        return "Adds a friend to your friends list. If this user has an outstanding friend request towards you, this request will be accepted.";
    }

    @Override
    public String getUsage()
    {
        return "/friend add (user)";
    }
}
