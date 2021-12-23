package be.dieterblancke.bungeeutilisalsx.common.api.party;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig.PartyRole;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig.PartyRolePermission;

import java.util.Optional;

public class PartyUtils
{

    private PartyUtils()
    {
    }

    public static boolean hasPermission( final Party party, final User user, final PartyRolePermission permission )
    {
        return party.getOwner().getUuid().equals( user.getUuid() ) || party.getPartyMembers()
                .stream()
                .filter( m -> m.getUuid().equals( user.getUuid() ) )
                .findAny()
                .map( m -> m.getPartyRole() != null && m.getPartyRole().getPermissions().contains( permission ) )
                .orElse( false );
    }

    public static String getRoleName( final Party party, final User user )
    {
        final String noRole = user.getLanguageConfig().getConfig().getString( "party.list.members.no-role" );

        return party.getPartyMembers()
                .stream()
                .filter( it -> it.getUuid().equals( user.getUuid() ) )
                .findFirst()
                .map( it ->
                {
                    if ( it.getUuid().equals( party.getOwner().getUuid() ) )
                    {
                        return user.getLanguageConfig().getConfig().getString( "party.owner-role-name" );
                    }
                    else
                    {
                        return Optional.ofNullable( it.getPartyRole() )
                                .map( PartyRole::getName )
                                .orElse( noRole );
                    }
                } )
                .orElse( noRole );
    }
}
