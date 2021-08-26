package be.dieterblancke.bungeeutilisalsx.webapi.queryresolvers.user;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.User;
import graphql.kickstart.tools.GraphQLResolver;
import org.springframework.stereotype.Component;

@Component
public class UserResolver implements GraphQLResolver<User>
{

    public Language getLanguage( final User user )
    {
        return BuX.getApi().getLanguageManager().getLangOrDefault( user.getLanguageId() );
    }
}