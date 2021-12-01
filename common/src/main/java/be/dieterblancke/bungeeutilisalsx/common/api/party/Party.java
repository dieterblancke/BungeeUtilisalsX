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
    private final int partyLimit;
    private boolean inactive;

    public Party( final Date createdAt, final int partyLimit )
    {
        this( UUID.randomUUID(), createdAt, partyLimit );
    }

    public PartyMember getOwner()
    {
        return partyMembers
                .stream()
                .filter( PartyMember::isPartyOwner )
                .findFirst()
                .orElseGet( () -> partyMembers.isEmpty() ? null : partyMembers.get( 0 ) );
    }

    public boolean isFull()
    {
        return partyMembers.size() >= partyLimit;
    }
}
