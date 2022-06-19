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
    private String languagePath;
    private Object[] placeholders;

    public UserLanguageMessageJob( final HasUserJob job,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        this( job.getUserName(), languagePath, placeholders );
    }

    public UserLanguageMessageJob( final HasUserJob job,
                                   final boolean prefix,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        this( job.getUserName(), prefix, languagePath, placeholders );
    }

    public UserLanguageMessageJob( final String userName,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        this( userName, true, languagePath, placeholders );
    }

    public UserLanguageMessageJob( final UUID uuid,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        this( uuid, null, true, languagePath, placeholders );
    }

    public UserLanguageMessageJob( final String userName,
                                   final boolean prefix,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        this( null, userName, prefix, languagePath, placeholders );
    }

    public UserLanguageMessageJob( final UUID uuid,
                                   final String userName,
                                   final boolean prefix,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        super( uuid, userName );

        this.prefix = prefix;
        this.languagePath = languagePath;
        this.placeholders = placeholders;
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
