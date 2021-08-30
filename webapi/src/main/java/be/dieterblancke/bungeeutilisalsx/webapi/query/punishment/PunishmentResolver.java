package be.dieterblancke.bungeeutilisalsx.webapi.query.punishment;

import be.dieterblancke.bungeeutilisalsx.webapi.caching.Cacheable;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.Punishment;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.User;
import be.dieterblancke.bungeeutilisalsx.webapi.service.UserService;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PunishmentResolver implements GraphQLResolver<Punishment>
{

    private final UserService userService;

    @Cacheable
    public User getExecutedBy( final Punishment punishment )
    {
        return userService.findByName( punishment.getExecutedById() );
    }
}