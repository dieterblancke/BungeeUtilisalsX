package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.api.job.HasUserJob;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserRemoveFriendJob extends HasUserJob
{

    private final String friendName;

    public UserRemoveFriendJob( final UUID uuid,
                                final String userName,
                                final String friendName )
    {
        super( uuid, userName );

        this.friendName = friendName;
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
