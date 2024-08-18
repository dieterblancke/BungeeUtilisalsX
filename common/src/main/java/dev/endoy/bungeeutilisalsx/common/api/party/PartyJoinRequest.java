package dev.endoy.bungeeutilisalsx.common.api.party;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PartyJoinRequest
{

    private final Date requestedAt;
    private final UUID requester;
    private final String requesterName;

    public static PartyJoinRequest fromMap( final UUID requesterUuid, final Map<String, String> requestData )
    {
        return new PartyJoinRequest(
            new Date( Long.parseLong( requestData.get( "requestedAt" ) ) ),
            requesterUuid,
            requestData.get( "requesterName" )
        );
    }

    public Map<String, String> asMap()
    {
        final Map<String, String> requestData = new HashMap<>();

        requestData.put( "invitedAt", String.valueOf( requestedAt.getTime() ) );
        requestData.put( "requesterName", requesterName );

        return requestData;
    }
}
