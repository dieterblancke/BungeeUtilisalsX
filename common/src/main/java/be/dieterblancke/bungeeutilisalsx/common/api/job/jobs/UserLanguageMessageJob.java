package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.HasUserJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class UserLanguageMessageJob implements MultiProxyJob
{

    private String userName;
    private boolean prefix;
    private boolean colorBefore;
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
        this( job.getUserName(), prefix, false, languagePath, placeholders );
    }

    public UserLanguageMessageJob( final HasUserJob job,
                                   final boolean prefix,
                                   final boolean colorBefore,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        this( job.getUserName(), prefix, colorBefore, languagePath, placeholders );
    }

    public UserLanguageMessageJob( final String userName,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        this( userName, true, false, languagePath, placeholders );
    }

    public UserLanguageMessageJob( final String userName,
                                   final boolean prefix,
                                   final boolean colorBefore,
                                   final String languagePath,
                                   final Object... placeholders )
    {
        this.userName = userName;
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

    public Optional<User> getUser()
    {
        return BuX.getApi().getUser( userName );
    }
}
