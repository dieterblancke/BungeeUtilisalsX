package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.job.HasUserJob;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserMessageJob extends HasUserJob
{

    private final String message;

    public UserMessageJob( final HasUserJob job,
                           final String message )
    {
        this( job.getUuid(), job.getUserName(), message );
    }

    public UserMessageJob( final UUID uuid,
                           final String userName,
                           final String message )
    {
        super( uuid, userName );
        this.message = message;
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
