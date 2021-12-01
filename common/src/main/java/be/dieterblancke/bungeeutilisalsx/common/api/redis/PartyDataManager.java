package be.dieterblancke.bungeeutilisalsx.common.api.redis;

import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyInvite;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyJoinRequest;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;

import java.util.List;

public interface PartyDataManager
{

    void registerParty( Party party );

    void unregisterParty( Party party );

    void addMemberToParty( Party party, PartyMember partyMember );

    void removeMemberFromParty( Party party, PartyMember partyMember );

    void setInactiveStatus( Party party, boolean inactive );

    void setInactiveStatus( Party party, PartyMember partyMember, boolean inactive );

    void setOwnerStatus( Party party, PartyMember partyMember, boolean owner );

    void setChatStatus( Party party, PartyMember partyMember, boolean chat );

    void addInviteToParty( Party party, PartyInvite partyInvite );

    void removeInviteFromParty( Party party, PartyInvite partyInvite );

    void addJoinRequestToParty( Party party, PartyJoinRequest partyJoinRequest );

    void removeJoinRequestFromParty( Party party, PartyJoinRequest partyJoinRequest );

    List<Party> getAllParties();

}
