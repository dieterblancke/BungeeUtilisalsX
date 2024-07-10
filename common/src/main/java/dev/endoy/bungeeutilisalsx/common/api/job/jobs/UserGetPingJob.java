package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.HasUserJob;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class UserGetPingJob extends HasUserJob
{

    private final String targetName;

    public UserGetPingJob( final UUID uuid,
                           final String userName,
                           final String targetName )
    {
        super( uuid, userName );
        this.targetName = targetName;
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }

    public Optional<User> getTargetUser()
    {
        return BuX.getApi().getUser( targetName );
    }
}
