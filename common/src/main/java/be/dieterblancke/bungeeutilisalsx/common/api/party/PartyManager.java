package be.dieterblancke.bungeeutilisalsx.common.api.party;

import be.dieterblancke.bungeeutilisalsx.common.api.party.exceptions.AlreadyInPartyException;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig.PartyRole;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;

import java.util.Optional;
import java.util.UUID;

public interface PartyManager
{

    Party createParty( User leader ) throws AlreadyInPartyException;

    Optional<Party> getCurrentPartyFor( String userName );

    Optional<Party> getCurrentPartyByUuid( UUID uuid );

    void removeParty( Party party );

    void removeParty( UUID uuid );

    void registerPartyLocally( Party party );

    void unregisterPartyLocally( Party party );

    void addMemberToParty( Party party, PartyMember member );

    void removeMemberFromParty( Party party, PartyMember member );

    void addInvitationToParty( Party party, PartyInvite invite );

    void removeInvitationFromParty( Party party, PartyInvite invite );

    void addJoinRequestToParty( Party party, PartyJoinRequest joinRequest );

    void removeJoinRequestFromParty( Party party, PartyJoinRequest joinRequest );

    void setPartyOwner( Party party, PartyMember member, boolean owner );

    void broadcastToParty( Party party, String message, HasMessagePlaceholders placeholders );

    void languageBroadcastToParty( Party party, String messagePath, HasMessagePlaceholders placeholders );

    void setChatMode( Party party, PartyMember partyMember, boolean chat );

    void setPartyMemberRole( Party party, PartyMember member, PartyRole partyRole );

}
