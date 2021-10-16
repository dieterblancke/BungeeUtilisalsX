package be.dieterblancke.bungeeutilisalsx.common.party;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyManager;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.party.exceptions.AlreadyInPartyException;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;

import java.util.*;

public class SimplePartyManager implements PartyManager
{

    private final List<Party> parties = Collections.synchronizedList( new ArrayList<>() );

    public SimplePartyManager()
    {
        // Party cleanup task
        BuX.getInstance().getScheduler().runTaskRepeating( 1, 1, TimeUnit.MINUTES, () ->
        {
            final List<Party> queuedForRemoval = new ArrayList<>();

            for ( Party party : parties )
            {
                final boolean partyInactive = party.getPartyMembers()
                        .stream()
                        .noneMatch( partyMember -> BuX.getApi().getPlayerUtils().isOnline( partyMember.getUserName() ) );

                if ( party.isInactive() && partyInactive )
                {
                    queuedForRemoval.add( party );
                }
                else
                {
                    party.setInactive( partyInactive );
                }
            }

            parties.removeIf( queuedForRemoval::contains );
        } );
    }

    @Override
    public Party createParty( final User leader ) throws AlreadyInPartyException
    {
        if ( getCurrentParty( leader.getName() ).isPresent() )
        {
            throw new AlreadyInPartyException();
        }

        final Party party = new Party( new Date() );
        party.getPartyMembers().add( new PartyMember(
                leader.getUuid(),
                leader.getName(),
                new Date(),
                leader.getName(),
                true
        ) );

        parties.add( party );
        // TODO: queue PartyCreateJob (or PartyCreateEvent)

        return party;
    }

    @Override
    public Optional<Party> getCurrentParty( final String userName )
    {
        return parties
                .stream()
                .filter( party -> party.getPartyMembers()
                        .stream()
                        .anyMatch( partyMember -> partyMember.getUserName().equals( userName ) ) )
                .findFirst();
    }

    @Override
    public void removeParty( final Party party )
    {
        // TODO: queue PartyRemovalJob (or PartyRemoveEvent)
    }
}
