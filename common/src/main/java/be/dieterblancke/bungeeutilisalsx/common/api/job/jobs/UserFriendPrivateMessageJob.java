package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.HasUserJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class UserFriendPrivateMessageJob extends HasUserJob
{

    private final String targetName;
    private final String message;
    private final PrivateMessageType type;

    public UserFriendPrivateMessageJob( final UUID uuid,
                                        final String userName,
                                        final String targetName,
                                        final String message,
                                        final PrivateMessageType type )
    {
        super( uuid, userName );
        this.targetName = targetName;
        this.message = message;
        this.type = type;
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
