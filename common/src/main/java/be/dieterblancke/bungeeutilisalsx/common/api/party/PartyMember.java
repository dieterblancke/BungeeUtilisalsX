package be.dieterblancke.bungeeutilisalsx.common.api.party;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PartyMember
{

    private final UUID uuid;
    private final String userName;
    private final Date joinedAt;
    private String nickName;
    private boolean partyOwner;
    private boolean inactive;

    public static PartyMember fromMap( final UUID partyMemberUuid, final Map<String, String> memberData )
    {
        return new PartyMember(
                partyMemberUuid,
                memberData.get( "userName" ),
                new Date( Long.parseLong( memberData.get( "joinedAt" ) ) ),
                memberData.get( "nickName" ),
                Boolean.parseBoolean( memberData.get( "partyOwner" ) ),
                Boolean.parseBoolean( memberData.get( "inactive" ) )
        );
    }

    public Map<String, String> asMap()
    {
        final Map<String, String> memberData = new HashMap<>();

        memberData.put( "userName", userName );
        memberData.put( "joinedAt", String.valueOf( joinedAt.getTime() ) );
        memberData.put( "nickName", nickName );
        memberData.put( "partyOwner", String.valueOf( partyOwner ) );
        memberData.put( "inactive", String.valueOf( inactive ) );

        return memberData;
    }
}
