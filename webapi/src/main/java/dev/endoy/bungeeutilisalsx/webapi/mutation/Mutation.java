package dev.endoy.bungeeutilisalsx.webapi.mutation;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao.ApiPermission;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.webapi.auth.RequiresPermission;
import dev.endoy.bungeeutilisalsx.webapi.dto.*;
import dev.endoy.bungeeutilisalsx.webapi.service.PunishmentService;
import dev.endoy.bungeeutilisalsx.webapi.service.UserService;
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
    private final PunishmentService punishmentService;

    @RequiresPermission( ApiPermission.UPDATE_USER )
    public User updateUser( final UUID uuid, final UpdateUserInput input )
    {
        final User user = userService.findByUuidUncached( uuid );

        BuX.getApi().getStorageManager().getDao().getUserDao().updateUser(
            uuid,
            Utils.blankToDefault( input.getUserName(), user.getUserName() ),
            Utils.blankToDefault( input.getIp(), user.getIp() ),
            BuX.getApi().getLanguageManager().getLangOrDefault( Utils.blankToDefault( input.getLanguage(), user.getLanguageId() ) ),
            input.getLastLogout() == null ? null : Timestamp.valueOf( input.getLastLogout() )
        );

        return userService.findByUuidUncached( uuid );
    }

    @RequiresPermission( ApiPermission.CREATE_PUNISHMENT )
    public Punishment createPunishment( final CreatePunishmentInput input )
    {
        return switch ( input.getType() )
        {
            case BAN -> punishmentService.createBan( input );
            case TEMPBAN -> punishmentService.createTempban( input );
            case IPBAN -> punishmentService.createIPBan( input );
            case IPTEMPBAN -> punishmentService.createIPTempban( input );
            case MUTE -> punishmentService.createMute( input );
            case TEMPMUTE -> punishmentService.createTempmute( input );
            case IPMUTE -> punishmentService.createIPMute( input );
            case IPTEMPMUTE -> punishmentService.createIPTempmute( input );
            default -> throw new UnsupportedOperationException( "This punishment type is not supported!" );
        };
    }

    @RequiresPermission( ApiPermission.REMOVE_PUNISHMENT )
    public void removePunishment( final RemovePunishmentInput input )
    {
        switch ( input.getType() )
        {
            case BAN, TEMPBAN -> punishmentService.removeCurrentBan( input );
            case IPBAN, IPTEMPBAN -> punishmentService.removeCurrentIpban( input );
            case MUTE, TEMPMUTE -> punishmentService.removeCurrentMute( input );
            case IPMUTE, IPTEMPMUTE -> punishmentService.removeCurrentIpmute( input );
            default -> throw new UnsupportedOperationException( "This punishment type is not supported!" );
        }
    }
}
