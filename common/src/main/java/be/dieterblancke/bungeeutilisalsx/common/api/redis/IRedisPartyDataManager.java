package be.dieterblancke.bungeeutilisalsx.common.api.redis;

import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyInvite;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyJoinRequest;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;

import java.util.List;

public interface IRedisPartyDataManager
{

    void registerParty( Party party );

    void unregisterParty( Party party );

    void addMemberToParty( Party party, PartyMember partyMember );

    void removeMemberFromParty( Party party, PartyMember partyMember );

    void addInviteToParty( Party party, PartyInvite partyInvite );

    void removeInviteToParty( Party party, PartyInvite partyInvite );

    void addJoinRequestToParty( Party party, PartyJoinRequest partyJoinRequest );

    void removeJoinRequestToParty( Party party, PartyJoinRequest partyJoinRequest );

    List<Party> getAllParties();

}
