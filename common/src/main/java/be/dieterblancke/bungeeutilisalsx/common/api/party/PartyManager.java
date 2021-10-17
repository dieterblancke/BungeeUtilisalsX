package be.dieterblancke.bungeeutilisalsx.common.api.party;

import be.dieterblancke.bungeeutilisalsx.common.api.party.exceptions.AlreadyInPartyException;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

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

}
