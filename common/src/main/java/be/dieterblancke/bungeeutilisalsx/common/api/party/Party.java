package be.dieterblancke.bungeeutilisalsx.common.api.party;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Party
{

    private final UUID uuid;
    private final Date createdAt;
    private final List<PartyMember> partyMembers = new ArrayList<>();
    private final List<PartyInvite> sentInvites = new ArrayList<>();
    private final List<PartyJoinRequest> joinRequests = new ArrayList<>();
    private boolean inactive;

    public Party( final Date createdAt )
    {
        this( UUID.randomUUID(), createdAt );
    }

    public PartyMember getOwner()
    {
        return partyMembers
                .stream()
                .filter( PartyMember::isPartyOwner )
                .findFirst()
                .orElseGet( () -> partyMembers.isEmpty() ? null : partyMembers.get( 0 ) );
    }
}
