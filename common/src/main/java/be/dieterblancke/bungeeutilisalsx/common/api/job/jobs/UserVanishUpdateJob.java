package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.job.HasUserJob;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class UserVanishUpdateJob extends HasUserJob
{

    private final boolean vanished;

    public UserVanishUpdateJob( final UUID uuid,
                                final String userName,
                                final boolean vanished )
    {
        super( uuid, userName );

        this.vanished = vanished;
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
