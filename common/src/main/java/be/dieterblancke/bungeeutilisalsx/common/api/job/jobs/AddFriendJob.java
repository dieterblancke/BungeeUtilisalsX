package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.job.HasUserJob;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class AddFriendJob extends HasUserJob
{

    private final UUID friendUuid;
    private final String friendName;
    private final Date friendsSince;
    private final Date lastLogout;

    public AddFriendJob( final UUID uuid,
                         final String userName,
                         final UUID friendUuid,
                         final String friendName,
                         final Date friendsSince,
                         final Date lastLogout )
    {
        super( uuid, userName );

        this.friendUuid = friendUuid;
        this.friendName = friendName;
        this.friendsSince = friendsSince;
        this.lastLogout = lastLogout;
    }

    public FriendData getAsFriendData()
    {
        return new FriendData( friendUuid, friendName, friendsSince, lastLogout );
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
