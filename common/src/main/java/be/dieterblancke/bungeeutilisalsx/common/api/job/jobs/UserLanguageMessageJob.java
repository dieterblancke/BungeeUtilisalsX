package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.job.HasUserJob;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserLanguageMessageJob extends HasUserJob
{

    private boolean prefix;
    private boolean colorBefore;
    private String languagePath;
    private Object[] placeholders;

    public UserLanguageMessageJob( final HasUserJob job,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        this( job.getUuid(), job.getUserName(), languagePath, placeholders );
    }

    public UserLanguageMessageJob( final HasUserJob job,
                                   final boolean prefix,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        this( job.getUuid(), job.getUserName(), prefix, false, languagePath, placeholders );
    }

    public UserLanguageMessageJob( final HasUserJob job,
                                   final boolean prefix,
                                   final boolean colorBefore,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        this( job.getUuid(), job.getUserName(), prefix, colorBefore, languagePath, placeholders );
    }

    public UserLanguageMessageJob( final UUID uuid,
                                   final String userName,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        this( uuid, userName, true, false, languagePath, placeholders );
    }

    public UserLanguageMessageJob( final UUID uuid,
                                   final String userName,
                                   final boolean prefix,
                                   final boolean colorBefore,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        super( uuid, userName );

        this.prefix = prefix;
        this.colorBefore = colorBefore;
        this.languagePath = languagePath;
        this.placeholders = placeholders;
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
