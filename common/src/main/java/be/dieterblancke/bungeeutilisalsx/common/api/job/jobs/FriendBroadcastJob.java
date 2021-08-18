package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class FriendBroadcastJob implements MultiProxyJob
{

    private final UUID senderUuid;
    private final String senderName;
    private final String message;
    private final List<String> receivers;

    public List<User> getReceivers()
    {
        return BuX.getApi().getUsers()
                .stream()
                .filter( user -> user.getName().equalsIgnoreCase( senderName )
                        || receivers.stream().anyMatch( name -> name.equalsIgnoreCase( user.getName() ) ) )
                .filter( user -> user.getFriendSettings().getSetting( FriendSetting.FRIEND_BROADCAST, true ) )
                .collect( Collectors.toList() );
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
