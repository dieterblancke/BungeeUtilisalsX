package be.dieterblancke.bungeeutilisalsx.webapi.mutation;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao.ApiPermission;
import be.dieterblancke.bungeeutilisalsx.webapi.auth.RequiresPermission;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.UpdateUserInput;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.User;
import be.dieterblancke.bungeeutilisalsx.webapi.service.UserService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Mutation implements GraphQLMutationResolver
{

    private final UserService userService;

    @RequiresPermission( ApiPermission.UPDATE_USER )
    public User updateUser( final UUID uuid, final UpdateUserInput input )
    {
        BuX.getApi().getStorageManager().getDao().getUserDao().updateUser(
                uuid,
                input.getUserName(),
                input.getIp(),
                BuX.getApi().getLanguageManager().getLangOrDefault( input.getLanguage() ),
                Timestamp.valueOf( input.getLastLogout() )
        );

        return userService.findByUuid( uuid );
    }
}
