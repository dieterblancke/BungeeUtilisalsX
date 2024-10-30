package dev.endoy.bungeeutilisalsx.common.api.party;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Party
{

    private final UUID uuid;
    private final Date createdAt;
    private final List<PartyMember> partyMembers = Lists.newCopyOnWriteArrayList();
    private final List<PartyInvite> sentInvites = Lists.newCopyOnWriteArrayList();
    private final List<PartyJoinRequest> joinRequests = Lists.newCopyOnWriteArrayList();
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

    public Optional<PartyMember> getMemberByUuid( final UUID uuid )
    {
        return partyMembers
            .stream()
            .filter( it -> it.getUuid().equals( uuid ) )
            .findAny();
    }

    public boolean isOwner( final UUID uuid )
    {
        return this.getOwner().getUuid().equals( uuid );
    }
}
