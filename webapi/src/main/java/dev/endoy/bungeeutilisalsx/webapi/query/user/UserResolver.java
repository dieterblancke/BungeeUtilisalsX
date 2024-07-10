package dev.endoy.bungeeutilisalsx.webapi.query.user;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.language.Language;
import dev.endoy.bungeeutilisalsx.webapi.dto.User;
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