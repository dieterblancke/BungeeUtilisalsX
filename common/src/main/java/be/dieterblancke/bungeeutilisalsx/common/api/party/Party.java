package be.dieterblancke.bungeeutilisalsx.common.api.party;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Party
{

    private final Date createdAt;
    private final List<PartyMember> partyMembers = new ArrayList<>();
    private final List<PartyInvite> sentInvites = new ArrayList<>();
    private final List<PartyJoinRequest> joinRequests = new ArrayList<>();
    private boolean inactive;

    public PartyMember getOwner()
    {
        return partyMembers
                .stream()
                .filter( PartyMember::isPartyOwner )
                .findFirst()
                .orElseGet( () -> partyMembers.isEmpty() ? null : partyMembers.get( 0 ) );
    }
}
