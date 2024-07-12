package dev.endoy.bungeeutilisalsx.common.api.redis;

import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyInvite;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyJoinRequest;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyMember;

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

    void setPartyMemberRole( Party party, PartyMember partyMember, String partyRole );

    void addInviteToParty( Party party, PartyInvite partyInvite );

    void removeInviteFromParty( Party party, PartyInvite partyInvite );

    void addJoinRequestToParty( Party party, PartyJoinRequest partyJoinRequest );

    void removeJoinRequestFromParty( Party party, PartyJoinRequest partyJoinRequest );

    List<Party> getAllParties();

}
