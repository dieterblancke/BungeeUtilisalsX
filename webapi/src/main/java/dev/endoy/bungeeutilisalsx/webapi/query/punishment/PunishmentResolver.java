package dev.endoy.bungeeutilisalsx.webapi.query.punishment;

import dev.endoy.bungeeutilisalsx.webapi.dto.Punishment;
import dev.endoy.bungeeutilisalsx.webapi.dto.User;
import dev.endoy.bungeeutilisalsx.webapi.service.UserService;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PunishmentResolver implements GraphQLResolver<Punishment>
{

    private final UserService userService;

    public User getExecutedBy( final Punishment punishment )
    {
        return userService.findByName( punishment.getExecutedById() );
    }
}