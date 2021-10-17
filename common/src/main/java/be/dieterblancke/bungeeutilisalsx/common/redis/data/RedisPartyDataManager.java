package be.dieterblancke.bungeeutilisalsx.common.redis.data;

import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyInvite;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyJoinRequest;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.redis.IRedisPartyDataManager;
import be.dieterblancke.bungeeutilisalsx.common.api.redis.RedisManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RedisPartyDataManager implements IRedisPartyDataManager
{

    private final RedisManager redisManager;

    @Override
    public void registerParty( Party party )
    {
        // TODO
    }

    @Override
    public void unregisterParty( Party party )
    {
        // TODO
    }

    @Override
    public void addMemberToParty( Party party, PartyMember partyMember )
    {
        // TODO
    }

    @Override
    public void removeMemberFromParty( Party party, PartyMember partyMember )
    {
        // TODO
    }

    @Override
    public void addInviteToParty( Party party, PartyInvite partyInvite )
    {
        // TODO
    }

    @Override
    public void removeInviteToParty( Party party, PartyInvite partyInvite )
    {
        // TODO
    }

    @Override
    public void addJoinRequestToParty( Party party, PartyJoinRequest partyJoinRequest )
    {
        // TODO
    }

    @Override
    public void removeJoinRequestToParty( Party party, PartyJoinRequest partyJoinRequest )
    {
        // TODO
    }

    @Override
    public List<Party> getAllParties()
    {
        // TODO
        return null;
    }
}
