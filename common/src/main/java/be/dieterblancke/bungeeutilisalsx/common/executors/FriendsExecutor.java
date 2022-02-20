package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserLoadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectedEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.base.Strings;

public class FriendsExecutor implements EventExecutor
{

    @Event
    public void onLoad( final UserLoadEvent event )
    {
        final User user = event.getUser();
        BuX.getApi().getStorageManager().getDao().getFriendsDao().getIncomingFriendRequests( user.getUuid() )
                .thenAccept( requests ->
                {
                    if ( !requests.isEmpty() )
                    {
                        user.sendLangMessage( "friends.join.requests", "{amount}", requests.size() );
                    }
                } );
    }

    @Event
    public void onFriendJoin( final UserLoadEvent event )
    {
        final User user = event.getUser();

        if ( user.isVanished() )
        {
            return;
        }

        user.getFriends().forEach( data -> BuX.getApi().getUser( data.getFriend() ).ifPresentOrElse(
                ( u ) -> u.sendLangMessage( "friends.join.join", "{user}", user.getName() ),
                () ->
                {
                    if ( BuX.getApi().getPlayerUtils().isOnline( data.getFriend() ) )
                    {
                        BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                                data.getFriend(),
                                "friends.join.join",
                                "{user}", user.getName()
                        ) );
                    }
                }
        ) );
    }

    @Event
    public void onFriendLeave( final UserUnloadEvent event )
    {
        final User user = event.getUser();

        if ( user.isVanished() )
        {
            return;
        }

        user.getFriends().forEach( data -> BuX.getApi().getUser( data.getFriend() ).ifPresentOrElse(
                ( u ) -> u.sendLangMessage( "friends.leave", "{user}", user.getName() ),
                () ->
                {
                    if ( BuX.getApi().getPlayerUtils().isOnline( data.getFriend() ) )
                    {
                        BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                                data.getFriend(),
                                "friends.leave",
                                "{user}", user.getName()
                        ) );
                    }
                }
        ) );
    }

    @Event
    public void onSwitch( final UserServerConnectedEvent event )
    {
        final User user = event.getUser();

        if ( user.isVanished() )
        {
            return;
        }

        if ( Strings.isNullOrEmpty( event.getTarget().getName() )
                || ConfigFiles.FRIENDS_CONFIG.isDisabledServerSwitch( event.getTarget().getName() ) )
        {
            return;
        }

        user.getFriends().forEach( data -> BuX.getApi().getUser( data.getFriend() ).ifPresentOrElse(
                ( u ) ->
                {
                    if ( u.getFriendSettings().getSetting( FriendSetting.SERVER_SWITCH ) )
                    {
                        u.sendLangMessage(
                                "friends.switch",
                                "{user}", user.getName(),
                                "{from}", user.getServerName(),
                                "{to}", event.getTarget().getName()
                        );
                    }
                },
                () -> BuX.getApi().getStorageManager().getDao().getFriendsDao().getSetting(
                        data.getUuid(),
                        FriendSetting.SERVER_SWITCH
                ).thenAccept( shouldSend ->
                {
                    if ( shouldSend )
                    {
                        BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                                data.getFriend(),
                                "friends.switch",
                                "{user}", user.getName(),
                                "{from}", user.getServerName(),
                                "{to}", event.getTarget().getName()
                        ) );
                    }
                } )
        ) );
    }
}
