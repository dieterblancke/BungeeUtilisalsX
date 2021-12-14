package be.dieterblancke.bungeeutilisalsx.common.api.party;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig.PartyRolePermission;

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
}
