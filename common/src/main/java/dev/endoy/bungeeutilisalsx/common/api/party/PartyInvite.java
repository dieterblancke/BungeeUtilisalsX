package dev.endoy.bungeeutilisalsx.common.api.party;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PartyInvite
{

    private final Date invitedAt;
    private final UUID invitee;
    private final String inviteeName;
    private final UUID invitedBy;

    public static PartyInvite fromMap( final UUID inviteeUuid, final Map<String, String> inviteData )
    {
        return new PartyInvite(
                new Date( Long.parseLong( inviteData.get( "invitedAt" ) ) ),
                inviteeUuid,
                inviteData.get( "inviteeName" ),
                UUID.fromString( inviteData.get( "invitedBy" ) )
        );
    }

    public Map<String, String> asMap()
    {
        final Map<String, String> inviteData = new HashMap<>();

        inviteData.put( "invitedAt", String.valueOf( invitedAt.getTime() ) );
        inviteData.put( "inviteeName", inviteeName );
        inviteData.put( "invitedBy", invitedBy.toString() );

        return inviteData;
    }
}
