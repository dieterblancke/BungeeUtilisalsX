package be.dieterblancke.bungeeutilisalsx.common.api.party;

import be.dieterblancke.bungeeutilisalsx.common.api.party.exceptions.AlreadyInPartyException;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.Optional;

public interface PartyManager
{

    Party createParty( User leader ) throws AlreadyInPartyException;

    Optional<Party> getCurrentParty( String userName );

    void removeParty( Party party );

}
